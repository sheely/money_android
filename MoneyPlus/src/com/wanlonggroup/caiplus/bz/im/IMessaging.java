package com.wanlonggroup.caiplus.bz.im;

public class IMessaging {

    public final String senderUserId;
    public final String senderUserName;
    public final String senderHeadIcon;
    public final String receiverUserId;
    public final String chatContent;
    public final String sendTime;

    public IMessaging(String senderUserId, String senderUserName, String senderHeadIcon,
            String receiverUserId, String chatContent, String sendTime) {
        this.senderUserId = senderUserId;
        this.senderUserName = senderUserName;
        this.senderHeadIcon = senderHeadIcon;
        this.receiverUserId = receiverUserId;
        this.chatContent = chatContent;
        this.sendTime = sendTime;
    }

    public IMessaged toIMessaged() {
        return new IMessaged(senderUserId, senderUserName, senderHeadIcon, receiverUserId,
                chatContent, sendTime);
    }
    
    public IMessage toIMessage(){
        return new IMessage(senderUserId, senderUserName, senderHeadIcon, receiverUserId,
            chatContent, sendTime);
    }

}
