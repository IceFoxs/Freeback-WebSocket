protoc --proto_path=. --java_out=../freeback/src FBConfigure.proto
protoc --proto_path=. --java_out=../freeback/src FBRegion.proto
protoc --proto_path=. --java_out=../freeback/src FBUser.proto
protoc --proto_path=. --java_out=../freeback/src FBMessage.proto

protoc --proto_path=. --java_out=../freeback/src FBStoreProperty.proto
protoc --proto_path=. --java_out=../freeback/src FBProduct.proto
protoc --proto_path=. --java_out=../freeback/src FBStore.proto
protoc --proto_path=. --java_out=../freeback/src FBOrder.proto
protoc --proto_path=. --java_out=../freeback/src FBSystem.proto
