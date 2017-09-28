package com.exchangeinfomanager.asinglestockinfo;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian;

public class SubnodeButton extends javax.swing.JButton{
    
    private int direction = BanKuaiAndChanYeLian.NONE;
        
    public void setDirection(int direction){
        this.direction = direction;
    }
    
    public int getDirection(){
        return direction;
    }
    
}

