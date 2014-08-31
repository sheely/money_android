package com.wanlonggroup.caiplus.bz.im;

public class IMessaged {
    public final String senderUserId;
    public final String senderUserName;
    public final String senderHeadIcon;
    public final String receiverUserId;
    public final String chatContent;
    public final String sendTime;

    public IMessaged(String senderUserId, String senderUserName, String senderHeadIcon,
            String receiverUserId, String chatContent, String sendTime) {
        this.senderUserId = senderUserId;
        this.senderUserName = senderUserName;
        this.senderHeadIcon = senderHeadIcon;
        this.receiverUserId = receiverUserId;
        this.chatContent = chatContent;
        this.sendTime = sendTime;
    }

}
