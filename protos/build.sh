protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries FBConfigure.proto
protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries FBRegion.proto
protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries FBUser.proto
protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries FBMessage.proto  

protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries/store FBStore.proto
protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries/store FBProduct.proto
protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries/store FBOrder.proto
protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries FBSystem.proto
protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries FBMasterFiles.proto
protoc --proto_path=. --objc_out=../../freeback-ios/ipad/FreeBack/Entries FBStoreDict.proto

protoc --proto_path=. --java_out=../freeback/src FBConfigure.proto
protoc --proto_path=. --java_out=../freeback/src FBRegion.proto
protoc --proto_path=. --java_out=../freeback/src FBUser.proto
protoc --proto_path=. --java_out=../freeback/src FBMessage.proto

protoc --proto_path=. --java_out=../freeback/src FBStore.proto
protoc --proto_path=. --java_out=../freeback/src FBProduct.proto
protoc --proto_path=. --java_out=../freeback/src FBOrder.proto
protoc --proto_path=. --java_out=../freeback/src FBSystem.proto
protoc --proto_path=. --java_out=../freeback/src FBMasterFiles.proto
protoc --proto_path=. --java_out=../freeback/src FBStoreDict.proto
