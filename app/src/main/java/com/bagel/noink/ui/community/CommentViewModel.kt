package com.bagel.noink.ui.community

import androidx.lifecycle.ViewModel
import com.bagel.noink.bean.CommentItemBean
import com.bagel.noink.bean.CommunityItemBean

class CommentViewModel: ViewModel() {
    companion object {
        var pid: Int = -1
        var communityItemBean: CommunityItemBean? = null
        var commentItemBean: CommentItemBean? = null
        fun updatePid(pid: Int){
            this.pid = pid
        }
        fun updateCommunityItemBean(communityItemBean: CommunityItemBean){
            this.communityItemBean = communityItemBean
        }
        fun updateCommentItemBean(commentItemBean: CommentItemBean){
            this.commentItemBean = commentItemBean
        }
    }


}