package net.freeback.datahelper.store;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import net.freeback.connection.MongoHelper;
import net.freeback.entries.store.FBProductProto;

/**
* Created with IntelliJ IDEA.
 * User: zhaotianyu
 * Date: 12-10-24
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class FBProductHelper {
	static final String TBL_STORE_PRODUCT = "store.product";
	static final String PRODUCT_RECORD_ID = "_Id";
	static final String PRODUCT_STORE = "_1";
	static final String PRODUCT_CATEGORY = "_2";
	static final String PRODUCT_CATEGORY_PRODUCTS = "_3";

	static final String PRODUCT_ID = "_1";
	static final String PRODUCT_NAME = "_2";
	static final String PRODUCT_ORDER = "_3";
	static final String PRODUCT_DESCRIPTION = "_4";
	static final String PRODUCT_MODIFIED = "_5";
	static final String PRODUCT_PHOTO = "_6";
	static final String PRODUCT_PHOTO_NAME = "_1";

	static final String PRODUCT_STOCK = "_7";
	static final String STOCK_DIMENSION = "_1";
	static final String STOCK_PRICE = "_2";
	static final String STOCK_COUNT = "_3";

	static FBProductProto.FBProduct buildProduct(BasicDBObject productObject) {
		FBProductProto.FBProduct.Builder builder = FBProductProto.FBProduct.newBuilder();
		builder.setCode(productObject.getString(PRODUCT_ID));
		builder.setName(productObject.getString(PRODUCT_NAME));
		builder.setOrder(productObject.getInt(PRODUCT_ORDER));
		builder.setModified(productObject.getDate(PRODUCT_MODIFIED).toString());
		builder.setDescription(productObject.getString(PRODUCT_DESCRIPTION));

		BasicDBList dbList;
		BasicDBObject basicDBObject;
		Object photos = productObject.get(PRODUCT_PHOTO);
		if (photos != null)
		{
			dbList = (BasicDBList)photos;
			for(int i = 0; i < dbList.size(); i ++)
			{
				basicDBObject = (BasicDBObject)dbList.get(i);
				builder.addPhotos(basicDBObject.getString(PRODUCT_PHOTO_NAME));
			}
		}

		Object stocks = productObject.get(PRODUCT_STOCK);
		if (stocks != null) {
			FBProductProto.FBStock.Builder stockBuilder = FBProductProto.FBStock.newBuilder();
			dbList = (BasicDBList) stocks;
			for (int i = 0; i < dbList.size(); i++) {
				BasicDBObject stockObject = (BasicDBObject) dbList.get(i);
				stockBuilder.setDimension(stockObject.getString(STOCK_DIMENSION));
				stockBuilder.setPrice((float) stockObject.getDouble(STOCK_PRICE));
				stockBuilder.setStocks(stockObject.getInt(STOCK_COUNT));
				builder.addStocks(stockBuilder);
			}
		}
		return builder.build();
	}

	static FBProductProto.FBCategoryProduct buildCategoryProduct(BasicDBObject categoryObject) {
		FBProductProto.FBCategoryProduct.Builder categoryProductBuilder = FBProductProto.FBCategoryProduct.newBuilder();
		categoryProductBuilder.setCategory(categoryObject.getInt(PRODUCT_CATEGORY));
		BasicDBList dbList = (BasicDBList)categoryObject.get(PRODUCT_CATEGORY_PRODUCTS);
		for(int i = 0; i < dbList.size(); i ++)
		{
			BasicDBObject productObject = (BasicDBObject)dbList.get(i);
			categoryProductBuilder.addProducts(buildProduct(productObject));
		}
		return categoryProductBuilder.build();
	}

	static public FBProductProto.FBCategoryProduct findByCategory(int store, int category){
		BasicDBObject query = new BasicDBObject();
		query.put(PRODUCT_STORE, store);
		query.put(PRODUCT_CATEGORY, category);
		BasicDBObject sort = new BasicDBObject(PRODUCT_ORDER, 1);
		DBCursor cursor = MongoHelper.sharedInstance().query(TBL_STORE_PRODUCT, query, sort);
		return cursor.hasNext() ? buildCategoryProduct((BasicDBObject)cursor.next()) : null;
	}

	static public FBProductProto.FBProducts findByStore(String store) throws InvalidProtocolBufferException {
		BasicDBObject query = new BasicDBObject(PRODUCT_STORE, store);
		BasicDBObject sort = new BasicDBObject(PRODUCT_CATEGORY, 1);
		sort.put(PRODUCT_ORDER, 1);
		DBCursor cursor = MongoHelper.sharedInstance().query(TBL_STORE_PRODUCT, query, sort);
		if (!cursor.hasNext()) return null;

		FBProductProto.FBProducts.Builder productsBuilder = FBProductProto.FBProducts.newBuilder();
		BasicDBObject storeProduct = (BasicDBObject)cursor.next();
		productsBuilder.setCode(storeProduct.getString(PRODUCT_RECORD_ID));
		productsBuilder.setStore(store);
		BasicDBList categoryList = (BasicDBList)storeProduct.get(PRODUCT_CATEGORY);
		for(int i = 0; i < categoryList.size(); i ++)
		{
			BasicDBObject categoryObject = (BasicDBObject)categoryList.get(i);
			productsBuilder.addProducts(buildCategoryProduct(categoryObject));
		}
		return productsBuilder.build();
	}

	static DBObject convertProduct(int store, int category, FBProductProto.FBProduct product) {
		BasicDBObject dbObject = new BasicDBObject();
		if (product.hasCode()) {
			dbObject.put(PRODUCT_ID, product.getCode());
		}
		dbObject.put(PRODUCT_STORE, store);
		dbObject.put(PRODUCT_CATEGORY, category);
		dbObject.put(PRODUCT_NAME, product.getName());
		dbObject.put(PRODUCT_DESCRIPTION, product.getDescription());
		dbObject.put(PRODUCT_MODIFIED, product.getModified());

		if (product.getStocksCount() > 0) {
			BasicDBList dbList = new BasicDBList();
			for (int i = 0; i < product.getStocksCount(); i++) {
				FBProductProto.FBStock stock = product.getStocks(i);
				BasicDBObject dimensionObject = new BasicDBObject();
				dimensionObject.put(STOCK_DIMENSION, stock.getDimension());
				dimensionObject.put(STOCK_PRICE, stock.getPrice());
				dimensionObject.put(STOCK_COUNT, stock.getStocks());
				dbList.add(dimensionObject);
			}
			dbObject.put(PRODUCT_STOCK, dbList);
		}
		return dbObject;
	}

	static public void save(int store, FBProductProto.FBCategoryProduct categoryProduct)
	{
		int category = categoryProduct.getCategory();
		BasicDBObject categoryObject = new BasicDBObject();
		categoryObject.put(PRODUCT_CATEGORY, category);

		BasicDBList productList = new BasicDBList();
		for(FBProductProto.FBProduct product : categoryProduct.getProductsList())
		{
			DBObject productObject = convertProduct(store, category, product);
			productList.add(productObject);
		}
		categoryObject.put(PRODUCT_CATEGORY_PRODUCTS, productList);

		BasicDBObject qObject = new BasicDBObject(PRODUCT_STORE, store);
		qObject.put(PRODUCT_CATEGORY, category);

		BasicDBObject storeObject = new BasicDBObject(PRODUCT_STORE, store);
		storeObject.put(PRODUCT_CATEGORY, categoryObject);
		MongoHelper.sharedInstance().update(TBL_STORE_PRODUCT, qObject, storeObject);
	}
}
