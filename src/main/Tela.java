/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 *
 * @author alirio
 */
public class Tela extends JPanel{
    private static final int W = 64;
    private static final int H = 32;
    private final int scale = 8;
    private int[][] gfx = new int[64][32];
    public boolean drawFlag;
    static int[] chip8_fontset = 
    { 
      0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
      0x20, 0x60, 0x20, 0x20, 0x70, // 1
      0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
      0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
      0x90, 0x90, 0xF0, 0x10, 0x10, // 4
      0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
      0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
      0xF0, 0x10, 0x20, 0x40, 0x40, // 7
      0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
      0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
      0xF0, 0x90, 0xF0, 0x90, 0x90, // A
      0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
      0xF0, 0x80, 0x80, 0x80, 0xF0, // C
      0xE0, 0x90, 0x90, 0x90, 0xE0, // D
      0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
      0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };
    
    public Tela(){
        
    }
    
    @Override
    public void paint(Graphics g){
        Graphics2D graphics2D = (Graphics2D) g;
        //Set  anti-alias!
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON); 
        for(int x=0;x<W;x++){
            for(int y=0;y<H;y++){
                if(getPixel(x,y) == 1){
                    graphics2D.setColor(Color.WHITE);
                }else{
                    graphics2D.setColor(Color.BLACK);
                }
                graphics2D.fillRect(x*scale, (y*scale), scale, scale);
            }
        }
        drawFlag = false;
    }
    
    public void clear(){
        for(int h=0;h<H;h++){
            for(int w=0;w<W;w++){
                gfx[w][h]=0;
            }
        }
    }
    public int getPixel(int x, int y) {
        return gfx[x][y];
    }
    public void setPixel(int x, int y) {
        gfx[x][y] ^= 1;
    }

    public boolean isDrawFlag() {
        return drawFlag;
    }

    public void setDrawFlag(boolean drawFlag) {
        this.drawFlag = drawFlag;
    }
    
}
