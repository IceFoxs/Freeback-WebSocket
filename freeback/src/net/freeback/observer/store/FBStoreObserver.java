package net.freeback.observer.store;

import com.google.protobuf.InvalidProtocolBufferException;
import net.freeback.datahelper.store.FBProductHelper;
import net.freeback.datahelper.store.FBStoreDictHelper;
import net.freeback.datahelper.store.FBStoreHelper;
import net.freeback.entries.FBMessageProto;
import net.freeback.entries.store.FBProductProto;
import net.freeback.entries.store.FBStoreDictProto;
import net.freeback.entries.store.FBStoreProto;
import net.freeback.observer.assistant.FBNotificationCenter;
import net.freeback.observer.assistant.FBObserver;
import net.freeback.observer.assistant.FBHelper;
import org.apache.mina.core.session.IoSession;

/**
 * Created with IntelliJ IDEA.
 * User: freeback
 * Date: 12-8-1
 * Time: 上午07:33
 * To change this template use File | Settings | File Templates.
 */
public class FBStoreObserver implements FBObserver {

	public FBStoreObserver() {
		FBNotificationCenter notificationCenter = FBNotificationCenter.sharedInstance();
		notificationCenter.addObserver(FBMessageProto.Notify.StoreSave, this);
        notificationCenter.addObserver(FBMessageProto.Notify.StoreFindById, this);
        notificationCenter.addObserver(FBMessageProto.Notify.StoreFindByBoss, this);
		notificationCenter.addObserver(FBMessageProto.Notify.StoreDict, this);
		notificationCenter.addObserver(FBMessageProto.Notify.StoreProduct, this);
	}

	@Override
	public void messageReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws InvalidProtocolBufferException {
		FBMessageProto.Header header = message.getHeader();
		switch (header.getNotify()) {
			case StoreSave:
				this.storeSaveReceived(fromSession, message);
				break;
            case StoreFindById:
                this.storeFindByIdReceived(fromSession, message);
                break;
            case StoreFindByBoss:
                this.storeFindByBossReceived(fromSession, message);
                break;
			case StoreDict:
				this.storeDictReceived(fromSession, message);
				break;
			case StoreProduct:
				this.storeProductReceived(fromSession, message);
				break;
		}
    }

	void storeDictReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws InvalidProtocolBufferException {
		FBMessageProto.Header.Builder headBuilder = message.getHeader().toBuilder();
		headBuilder.setResponse(true);
		FBStoreDictProto.FBStoreDict storeDict = null;
		switch (headBuilder.getAction()) {
			case Find:
                storeDict = FBStoreDictHelper.find(headBuilder.getText());
				break;
			case Save:
                storeDict = FBStoreDictProto.FBStoreDict.parseFrom(message.getBodies());
				FBStoreDictHelper.save(storeDict);
                storeDict = FBStoreDictHelper.find(storeDict.getStore());
				break;
			case Remove:
				FBStoreDictHelper.remove(headBuilder.getText());
				break;
		}
		fromSession.write(FBHelper.buildMessage(headBuilder.build(), storeDict == null ? null : storeDict.toByteString()));
	}

//	void storeListReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws InvalidProtocolBufferException {
//		FBMessageProto.Header.Builder headBuilder = message.getHeader().toBuilder();
//		String boss = headBuilder.getSender();
//		FBStoreProto.FBStores stores = FBStoreHelper.getStoreList(boss);
//		headBuilder.setResponse(stores != null && stores.getStoresCount() > 0);
//		fromSession.write(FBHelper.buildMessage(headBuilder.build(), stores != null && stores.getStoresCount() > 0 ? stores.toByteString() : null));
//	}

    void storeFindByIdReceived(IoSession fromSession, FBMessageProto.FBMessage message)
    {
        FBMessageProto.Header.Builder headBuilder = message.getHeader().toBuilder();
        FBStoreProto.FBStore store = FBStoreHelper.findById(headBuilder.getText());
        headBuilder.setResponse(store != null);
        fromSession.write(FBHelper.buildMessage(headBuilder.build(), store == null ? null : store.toByteString()));
    }

    void storeFindByBossReceived(IoSession fromSession, FBMessageProto.FBMessage message)
    {
        FBMessageProto.Header.Builder headBuilder = message.getHeader().toBuilder();
        FBStoreProto.FBStores stores = FBStoreHelper.findByBoss(headBuilder.getText());
        FBStoreProto.FBStore store =  stores.getStoresCount() == 0 ? null : stores.getStores(0);
        headBuilder.setResponse(store != null);
        fromSession.write(FBHelper.buildMessage(headBuilder.build(), store == null ? null : store.toByteString()));
    }

    void storeSaveReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws InvalidProtocolBufferException {
		FBMessageProto.Header.Builder headBuilder = message.getHeader().toBuilder();
		String storeCode = FBStoreHelper.save(FBStoreProto.FBStore.parseFrom(message.getBodies()));
		headBuilder.setResponse(true);
        headBuilder.setText(storeCode);
		fromSession.write(FBHelper.buildMessage(headBuilder.build(), FBStoreHelper.findById(storeCode).toByteString()));
	}

	void storeProductReceived(IoSession fromSession, FBMessageProto.FBMessage message) throws InvalidProtocolBufferException {
		FBMessageProto.Header.Builder headBuilder = message.getHeader().toBuilder();
		headBuilder.setResponse(true);
		switch (headBuilder.getAction()) {
			case Find:
	    		FBProductProto.FBProducts products = FBProductHelper.findByStore(headBuilder.getText());
				fromSession.write(FBHelper.buildMessage(headBuilder.build(), products.toByteString()));
				break;
			case Save:
				FBProductProto.FBCategoryProduct categoryProduct = FBProductProto.FBCategoryProduct.parseFrom(message.getBodies());
				FBProductHelper.save(Integer.parseInt(headBuilder.getText()), categoryProduct);
				fromSession.write(FBHelper.buildMessage(headBuilder.build(), null));
				break;
		}
	}
}
