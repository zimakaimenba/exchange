package com.exchangeinfomanager.commonlib;

import java.awt.BorderLayout;   
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;   
import java.awt.GraphicsEnvironment;  
import java.awt.Insets;  
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;  
import javax.swing.Icon;  
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;  
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;  
import javax.swing.JWindow;  
import javax.swing.border.EtchedBorder;

import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;   
//https://xbgd.iteye.com/blog/755002
public class ReminderPopToolTip 
{  
  
      
    private int _width = 300;  
  
      
    private int _height = 200;//Short.MAX_VALUE;
  
    
    private int _step = 30;  
  
    
    private int _stepTime = 30;  
  
   
    private int _displayTime = 16000;  
  
  
    private int _countOfToolTip = 0;  
  
   
    private int _maxToolTip = 0;  
  
     
    private int _maxToolTipSceen;  
    
    private int _positionx;
    private int _positiony;
  
    // 字体  
    private Font _font;  
  
    // 边框颜色  
    private Color _bgColor;  
  
    // 背景颜色  
    private Color _border;  
  
    // 消息颜色  
    private Color _messageColor;  
  
    // 差�?�设�?  
    int _gap;  
  
    // 是否要求至顶（jre1.5以上版本方可执行�?  
    boolean _useTop = true;  
  
    /** 
     * 构�?�函数，初始化默认气泡提示设�? 
     * 
     */  
    public ReminderPopToolTip() 
    {  
        // 设定字体  
        _font = new Font(null, 0, 12);  
        // 设定边框颜色  
        _bgColor = new Color(255, 255, 225);  
        _border = Color.BLACK;  
        _messageColor = Color.BLACK;  
        _useTop = true;  
        // 通过调用方法，强制获知是否支持自动窗体置�?  
        try {  
            JWindow.class.getMethod("setAlwaysOnTop",  
                    new Class[] { Boolean.class });  
        } catch (Exception e) {  
            _useTop = false;  
        }  
  
    } 
    
  
  
    /** 
     * 重构JWindow用于显示单一气泡提示�? 
     * 
     */  
    class ToolTipSingle extends JWindow 
    {  
        private static final long serialVersionUID = 1L;  
  
        private JLabel _iconLabel = new JLabel();  
  
        private JTextArea _message = new JTextArea();  
        
        private JButton _openMatrixButn = new JButton ("");

        public ToolTipSingle() {  
            initComponents();  
            createEvents ();
        }  
  
        private void createEvents() 
        {
        	_openMatrixButn.addMouseListener(new MouseAdapter() {
            	@Override
            	public void mouseClicked(MouseEvent arg0) 
            	{
            		String bkfxmatrixfile = SystemConfigration.getInstance().getBanKuaiFengXiChecklistXmlFile();
            		try {
    					String cmd = "rundll32 url.dll,FileProtocolHandler " + bkfxmatrixfile;
    					Process p  = Runtime.getRuntime().exec(cmd);
    					p.waitFor();
    				} catch (Exception e1) {
    					// TODO Auto-generated catch block
    				    e1.printStackTrace();
    				}
            	}
            });
			
		}

		private void initComponents() {  
            setSize(_width, _height);  
            _message.setFont(getMessageFont());  
            JPanel externalPanel = new JPanel(new BorderLayout(1, 1));  
            externalPanel.setBackground(_bgColor);  
            
            //   
            JPanel innerPanel = new JPanel(new BorderLayout(getGap(), getGap()));  
            innerPanel.setBackground(_bgColor);  
            _message.setBackground(_bgColor);  
            _message.setMargin(new Insets(4, 4, 4, 4));  
            _message.setLineWrap(true);  
            _message.setWrapStyleWord(true);  
            //   
            EtchedBorder etchedBorder = (EtchedBorder) BorderFactory  
                    .createEtchedBorder();  
            //   
            externalPanel.setBorder(etchedBorder);  
            //   
            externalPanel.add(innerPanel);  
            _message.setForeground(getMessageColor());  
            innerPanel.add(_iconLabel, BorderLayout.WEST);  
            
            JScrollPane scrollmessage = new JScrollPane();
            scrollmessage.setViewportView(_message);
            
            innerPanel.add(scrollmessage, BorderLayout.CENTER);  
            
            _openMatrixButn.setToolTipText("��Matrix");
            _openMatrixButn.setIcon(new ImageIcon(StockInfoManager.class.getResource("/images/endit-16.png")) );
            JPanel buttonpnl = new JPanel ();
            buttonpnl.setBackground(_bgColor);
            buttonpnl.setLayout(new FlowLayout() );
            buttonpnl.add(_openMatrixButn);
            innerPanel.add(buttonpnl, BorderLayout.EAST);
            
            getContentPane().add(externalPanel);  
        }  
  
        /** 
         *  
         * 
         */  
        public void animate() {  
            new Animation(this).start();  
        }  
  
    }  
  
    /** 
     *  
     * 
     */  
    class Animation extends Thread {  
  
        ToolTipSingle _single;  
  
        public Animation(ToolTipSingle single) {  
            this._single = single;  
        }  
  
        /** 
         *  
         * 
         * @param posx 
         * @param startY 
         * @param endY 
         * @throws InterruptedException 
         */  
        private void animateVertically(int posx, int startY, int endY)  
                throws InterruptedException {  
            _single.setLocation(posx, startY);  
            if (endY < startY) {  
                for (int i = startY; i > endY; i -= _step) {  
                    _single.setLocation(posx, i);  
                    Thread.sleep(_stepTime);  
                }  
            } else {  
                for (int i = startY; i < endY; i += _step) {  
                    _single.setLocation(posx, i);  
                    Thread.sleep(_stepTime);  
                }  
            }  
            _single.setLocation(posx, endY);  
        }  
  
        /** 
         *  
         */  
        public void run() {  
            try {  
                boolean animate = false;  
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();  
                Rectangle screenRect = ge.getMaximumWindowBounds();  
                int screenHeight = (int) screenRect.height;  
                int startYPosition;  
                int stopYPosition;  
                if (screenRect.y > 0) {  
                    animate = false;  
                }  
                _maxToolTipSceen = screenHeight / _height;  
                
//                int posx = (int) screenRect.width - _width - 1;
                int posx = 0;  
                _single.setLocation(posx, screenHeight);  
                
                _single.setVisible(true);  
                
                if (_useTop) {  
                    _single.setAlwaysOnTop(true);  
                }  
                if (animate) {  
//                    startYPosition = screenHeight;
                	startYPosition = 0;
                    stopYPosition = startYPosition - _height - 1;  
                    if (_countOfToolTip > 0) {  
                        stopYPosition = stopYPosition  
                                - (_maxToolTip % _maxToolTipSceen * _height);  
                    } else {  
                        _maxToolTip = 0;  
                    }  
                } else {  
                    startYPosition = screenRect.y - _height;  
                    stopYPosition = screenRect.y;  
  
                    if (_countOfToolTip > 0) {  
                        stopYPosition = stopYPosition  
                                + (_maxToolTip % _maxToolTipSceen * _height);  
                    } else {  
                        _maxToolTip = 0;  
                    }  
                }  
  
                _countOfToolTip++;  
                _maxToolTip++;  
  
                animateVertically(posx, startYPosition, stopYPosition);  
                Thread.sleep(_displayTime);  
                animateVertically(posx, stopYPosition, startYPosition);  
  
                _countOfToolTip--;  
                _single.setVisible(false);  
                _single.dispose();  
            } catch (Exception e) {  
                throw new RuntimeException(e);  
            }  
        }  
    }  
  
    /** 
     *  
     * 
     * @param icon 
     * @param msg 
     */  
    public void setToolTip(Icon icon, String msg) {  
        try {  
            //   
            Thread.sleep(100);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
        ToolTipSingle single = new ToolTipSingle();  
        if (icon != null) {  
            single._iconLabel.setIcon(icon);  
        }  
        single._message.setText(msg);  
        Dimension size2 = new Dimension(300, (single._message).getPreferredSize().height + 10 );
        (single._message).setPreferredSize(size2);
        (single._message).setMinimumSize(size2);
        (single._message).setMaximumSize(size2);
        single.animate();  
    }  
    /** 
     *  
     * 
     * @param msg 
     */  
    public void setToolTip(String msg) {  
        setToolTip(null, msg);  
    }
    
    /** 
     *  
     * 
     * @return 
     */  
    public Font getMessageFont() {  
        return _font;  
    }  
    /**
     * 
     */
    public void setDisplayPosition (int x, int y)
    {
    	this._positionx = x;
    	this._positiony = y;
    }
    /** 
     *  
     * 
     * @param font 
     */  
    public void setMessageFont(Font font) {  
        _font = font;  
    }  
  
    /** 
     *  
     * 
     * @return 
     */  
    public Color getBorderColor() {  
        return _border;  
    }  
  
    /** 
     *  
     * 
     * @param _bgColor 
     */  
    public void setBorderColor(Color borderColor) {  
        this._border = borderColor;  
    }  
  
    /** 
     *  
     * 
     * @return 
     */  
    public int getDisplayTime() {  
        return _displayTime;  
    }  
  
    /** 
     *  
     * 
     * @param displayTime 
     */  
    public void setDisplayTime(int displayTime) {  
        this._displayTime = displayTime;  
    }  
  
    /** 
     *  
     * 
     * @return 
     */  
    public int getGap() {  
        return _gap;  
    }  
  
    /** 
     *  
     * 
     * @param gap 
     */  
    public void setGap(int gap) {  
        this._gap = gap;  
    }  
  
    /** 
     *  
     * 
     * @return 
     */  
    public Color getMessageColor() {  
        return _messageColor;  
    }  
  
    /** 
     *  
     * 
     * @param messageColor 
     */  
    public void setMessageColor(Color messageColor) {  
        this._messageColor = messageColor;  
    }  
  
    /** 
     *  
     * 
     * @return 
     */  
    public int getStep() {  
        return _step;  
    }  
  
    /** 
     *  
     * 
     * @param _step 
     */  
    public void setStep(int _step) {  
        this._step = _step;  
    }  
  
    public int getStepTime() {  
        return _stepTime;  
    }  
  
    public void setStepTime(int _stepTime) {  
        this._stepTime = _stepTime;  
    }  
  
    public Color getBackgroundColor() {  
        return _bgColor;  
    }  
  
    public void setBackgroundColor(Color bgColor) {  
        this._bgColor = bgColor;  
    }  
  
    public int getHeight() {  
        return _height;  
    }  
  
    public void setHeight(int height) {  
        this._height = height;  
    }  
  
    public int getWidth() {  
        return _width;  
    }  
  
    public void setWidth(int width) {  
        this._width = width;  
    }  
  
    public static void main(String[] args) {  
  
    	ReminderPopToolTip tip = new ReminderPopToolTip();
    	
        tip.setToolTip(new ImageIcon("F:\\a.jpg"),"���Ե�ʱ��\n");  
        tip.setToolTip(new ImageIcon("F:\\a.jpg"),"���Ե�ʱ��\n���Ե�ʱ��\n");
        
  
    }  
  
}  