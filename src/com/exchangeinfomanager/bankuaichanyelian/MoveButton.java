package com.exchangeinfomanager.bankuaichanyelian;


public class MoveButton extends javax.swing.JButton{

    private int direction = BanKuaiAndChanYeLian.NONE;
        
    public void setDirection(int direction){
        this.direction = direction;
    }
    
    public int getDirection(){
        return direction;
    }
    
}