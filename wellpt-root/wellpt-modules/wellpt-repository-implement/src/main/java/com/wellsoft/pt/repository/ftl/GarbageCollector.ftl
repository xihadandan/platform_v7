db.getCollection('${filesCollectionName}').find().forEach(function(file) {
if(db.getCollection('repo_file_mirror').find({physical_file_id : file._id}).count() === 0) {
db.getCollection('${filesCollectionName}').remove({_id : file._id});
db.getCollection('${chunksCollectionName}').remove({files_id : file._id});
}
});