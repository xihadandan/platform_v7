/**
 * 添加批注(选取一段文字进行批注，批注人为当前的审阅人)
 * @constructor
 */
function OnAddCommentsClicked() {
    var doc = wps.WpsApplication().ActiveDocument;
    var s = doc.ActiveWindow.Selection;
    var username = wps.WpsApplication().UserName;
    if (doc) {
        var curComment = doc.Comments.Add(s.Range, s.Range.Text);
        curComment.Author = username;
        // wps.WpsApplication().CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Disable"); //去掉批注框中的可编辑组件，使其不可用
    }


}

/**
 * 显示当前审阅人的批注
 */
function onShowCommentsClicked() {
    var doc = wps.WpsApplication().ActiveDocument;
    var username = wps.WpsApplication().UserName;
    var comments = doc.Comments;
    var reviewers = doc.ActiveWindow.View.Reviewers;
    // wps.WpsApplication().CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Disable"); //去掉批注框中的可编辑组件，使其不可用
    if (doc) {
        if (comments) {
            var count = comments.Count;
            var reviewersCount = reviewers.Count;
            for (var i = 1; i <= count; i++) {
                var comment = comments.Item(i);
                if (comment) {
                    var rev = reviewers.Item(comment.Author)
                    if (comment.Author == username) {
                        rev.Visible = true;
                    } else {
                        rev.Visible = false;
                    }

                }
            }
        }
    }
}