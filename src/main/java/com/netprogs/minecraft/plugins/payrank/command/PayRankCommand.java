package com.netprogs.minecraft.plugins.payrank.command;

import com.netprogs.minecraft.plugins.payrank.command.PayRankPermissions.PayRankPermission;
import com.netprogs.minecraft.plugins.payrank.player.PlayerInfoUtil;

/*
 * Copyright 2012 Scott Milne. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

public abstract class PayRankCommand implements IPayRankCommand {

    private PayRankPermission permission;
    private PayRankPermission permissionOthers;
    private String commandName;
    private PlayerInfoUtil playerInfoUtil;

    protected PayRankCommand(String commandName, PayRankPermission permission) {

        this.playerInfoUtil = new PlayerInfoUtil();

        this.permission = permission;
        this.commandName = commandName;
    }

    protected PayRankCommand(String commandName, PayRankPermission permission, PayRankPermission permissionOthers) {

        this.playerInfoUtil = new PlayerInfoUtil();

        this.permission = permission;
        this.permissionOthers = permissionOthers;
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    protected PlayerInfoUtil getPlayerInfoUtil() {
        return playerInfoUtil;
    }

    public boolean match(String commandName) {

        return (commandName.equalsIgnoreCase(this.commandName));
    }

    public PayRankPermission getPermission() {
        return permission;
    }

    public PayRankPermission getPermissionOthers() {
        return permissionOthers;
    }
}
