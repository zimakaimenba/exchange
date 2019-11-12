package com.exchangeinfomanager.bankuaichanyelian;



public class MoveButton extends javax.swing.JButton{

    private int direction = BanKuaiAndChanYeLianGUI.NONE;
        
    public void setDirection(int direction){
        this.direction = direction;
    }
    
    public int getDirection(){
        return direction;
    }
    
}
