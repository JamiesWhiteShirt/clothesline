package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class DummyMessageHandler implements IMessageHandler<IMessage, IMessage> {
    @Override
    @Nullable
    public IMessage onMessage(IMessage message, MessageContext ctx) {
        throw new UnsupportedOperationException("This message handler should never have been called");
    }
}
