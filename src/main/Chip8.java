/*
 * Copyright (C) 2019 Alirio Oliveira <https://github.com/Alirio926>
 *
 * Este software é livre: você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (a seu critério) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser útil,
 * mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO
 * a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja <http://www.gnu.org/licenses/>.
 */

package main;

import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.Tela.chip8_fontset;

/**
 *
 * @author Alirio Oliveira <https://github.com/Alirio926>
 */

public class Chip8 {
    
    private static final Logger LOGGER = Logger.getLogger( Chip8.class.getName() );
    private Random random = new Random();    
    public int delay_timer;
    public int sound_timer;
    public int SP;
    public int I;
    public int PC;
    public int OpCode;
    public int[] V;
    public int[] stack;
    public int[] memory;
    private int Vx;
    private int Vy;
    private int kk;
    private int nnn;
    public Teclado teclado;
    public Tela screen;
    private int ciclos=0;
    
    public Chip8(Tela screen, Teclado teclado){
        this.memory = new int[4096];
        this.V = new int[16];
        this.stack =  new int[16];
        
        this.PC = 0x200;
        this.I = 0;
        this.OpCode = 0;
        this.SP = 0;        
                
        this.teclado = teclado;
        this.screen = screen;
        
        screen.setDrawFlag(false);
        System.arraycopy(chip8_fontset, 0, this.memory, 0, chip8_fontset.length);
    }
    public void resetChip8(){
        for(int i=0;i<16;i++){
            V[i] = 0;
            stack[i] = 0;
        }
        this.PC = 0x200;
        this.I = 0;
        this.OpCode = 0;
        this.SP = 0;
        screen.setDrawFlag(false);
        System.arraycopy(chip8_fontset, 0, this.memory, 0, chip8_fontset.length);
    }
    public void loadFile(File file) throws FileNotFoundException, IOException{
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        for(int i=0;i<file.length();i++){
            this.memory[i+512] = in.readUnsignedByte();
        }
        //LOGGER.log(Level.INFO,"#{0}{1}",new Object[]{Integer.toHexString(memory[512]),Integer.toHexString(memory[513])});
    }
    public void startTimers(){
        new Thread(() -> {
            while(true){
                if(delay_timer>0)delay_timer--;
                try{
                    Thread.sleep(1000/60);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Chip8.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        new Thread(()->{
            while(true){
                if(sound_timer>0){
                    Toolkit.getDefaultToolkit().beep();
                    sound_timer--;
                }
                try{
                    Thread.sleep(1000/60);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Chip8.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
    public void timers() {
        if (delay_timer > 0) delay_timer--;
        if (sound_timer > 0) sound_timer--;
    }
    public void fetch(){
        this.OpCode = (int)((memory[PC] << 8)| memory[PC+1]);
        this.Vx  = (OpCode & 0x0F00)>>8;
        this.Vy  = (OpCode & 0x00F0)>>4;
        this.kk  = OpCode & 0x00FF;
        this.nnn = OpCode & 0x0FFF;
        //LOGGER.log(Level.INFO, "\nOpCode: [{4}] | Registradores---\nVx\t Vy\t nn\t nnn\t\n{0}\t {1}\t {2}\t {3}", new Object[]{
        //Integer.toHexString(Vx).toUpperCase(), Integer.toHexString(Vy).toUpperCase(), Integer.toHexString(nn).toUpperCase(), Integer.toHexString(nnn).toUpperCase(), Integer.toHexString(OpCode).toUpperCase()});
        //debug();
    }
    public void execute(){
        switch(OpCode & 0xF000){
            case 0x0000: {
                switch(OpCode & 0x000F){
                    case 0x0000: { this.instrucao_00E0(); }break;                    
                    case 0x000E: { this.instrucao_00EE(); }break;
                    default: { LOGGER.log(Level.WARNING,"OpCode [0xF000]: {0} desconhecido.", Integer.toHexString(OpCode).toUpperCase()); }
                }
            }break;
            case 0x1000: { instrucao_1NNN(); }break;            
            case 0x2000: { instrucao_2NNN(); }break;
            case 0x3000: { instrucao_3XKK(); }break;
            case 0x4000: { instrucao_4XKK(); }break;
            case 0x5000: { instrucao_5XY0(); }break;
            case 0x6000: { instrucao_6XKK(); }break;
            case 0x7000: { instrucao_7XKK(); }break;
            case 0x8000: { 
                switch(OpCode & 0x000F){
                    case 0x0000:{ instrucao_8XY0(); } break;
                    case 0x0001:{ instrucao_8XY1(); } break;
                    case 0x0002:{ instrucao_8XY2(); } break;
                    case 0x0003:{ instrucao_8XY3(); } break;
                    case 0x0004:{ instrucao_8XY4(); } break;
                    case 0x0005:{ instrucao_8XY5(); } break;
                    case 0x0006:{ instrucao_8XY6(); } break;
                    case 0x0007:{ instrucao_8XY7(); } break;
                    case 0x000E:{ instrucao_8XYE(); } break;
                    default: { LOGGER.log(Level.WARNING,"OpCode [0x8000]: {0} desconhecido.", Integer.toHexString(OpCode).toUpperCase()); }
                }
            }break;
            case 0x9000: { instrucao_9XY0(); }break;
            case 0xA000: { instrucao_ANNN(); }break;
            case 0xB000: { instrucao_BNNN(); }break;
            case 0xC000: { instrucao_CXKK(); }break;
            case 0xD000: { instrucao_DXYN(); }break;
            case 0xE000: { 
                switch(OpCode & 0x00FF){
                    case 0x009E:{ instrucao_EX9E(); }break;
                    case 0x00A1:{ instrucao_EXA1(); }break;
                    default: { LOGGER.log(Level.WARNING,"OpCode [0xE000]: {0} desconhecido.", Integer.toHexString(OpCode).toUpperCase()); }
                }
            }break;
            case 0xF000: { 
                switch(OpCode & 0x00FF){
                    case 0x0007:{ instrucao_FX07(); }break;
                    case 0x000A:{ instrucao_FX0A(); }break;
                    case 0x0015:{ instrucao_FX15(); }break;
                    case 0x0018:{ instrucao_FX18(); }break;
                    case 0x001E:{ instrucao_FX1E(); }break;
                    case 0x0029:{ instrucao_FX29(); }break;
                    case 0x0033:{ instrucao_FX33(); }break;
                    case 0x0055:{ instrucao_FX55(); }break;
                    case 0x0065:{ instrucao_FX65(); }break;
                    default: { LOGGER.log(Level.WARNING,"OpCode [0xF000]: {0} desconhecido.", Integer.toHexString(OpCode).toUpperCase()); }
                }
            }break;
            default: { LOGGER.log(Level.WARNING,"OpCode: {0} desconhecido.", Integer.toHexString(OpCode).toUpperCase()); }
        }
        timers();
        /*try {
            Thread.sleep(1000/100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
    private void incProgramCount(){ this.PC += 2; }
    private void SkipNextInstrucao(){ this.PC += 4; }
    
    // CLS - Clear the display.
    private void instrucao_00E0(){
        screen.clear();
        screen.setDrawFlag(true);
        incProgramCount();
    }
    // RET - Return from a subroutine.
    private void instrucao_00EE(){
        this.PC = this.stack[--this.SP];
        //this.SP -= 1;
        incProgramCount();
    }
    // JP addr - Jump to location nnn.
    private void instrucao_1NNN(){
        this.PC = this.nnn;
        //incProgramCount();
    }
    // CALL addr - Call subroutine at nnn.
    private void instrucao_2NNN(){
        this.stack[this.SP] = this.PC;
        SP += 1;
        this.PC = this.nnn;
    }
    // SE Vx, byte - Skip next instruction if Vx = kk.
    private void instrucao_3XKK(){
        if(V[Vx] == kk)
            SkipNextInstrucao();
        else{
            incProgramCount();
        }
    }
    // SNE Vx, byte - Skip next instruction if Vx != kk.
    private void instrucao_4XKK(){
        if(V[Vx] != kk)
            SkipNextInstrucao();
        else
            incProgramCount();        
    }
    // SE Vx, Vy - Skip next instruction if Vx = Vy.
    private void instrucao_5XY0() {
        if(V[Vx] == V[Vy])
            SkipNextInstrucao();
        else
            incProgramCount();
    }
    // LD Vx, byte - Set Vx = kk.
    private void instrucao_6XKK() {
        V[Vx] = kk;
        incProgramCount();
    }
    // ADD Vx, byte - Set Vx = Vx + kk.
    private void instrucao_7XKK() {
        V[0xf] = 0;
        int sum = V[Vx] + kk;
        if(sum >= 256){ // soluciona overflow já que java não possui unsigned >< triste
            V[0xf] = 1;
            sum -= 256;
        }
        V[Vx] = sum;
        incProgramCount();
    }
    // LD Vx, Vy - Set Vx = Vy.
    private void instrucao_8XY0() {
        V[Vx] = V[Vy];
        incProgramCount();
    }
    // OR Vx, Vy - Set Vx = Vx OR Vy.
    private void instrucao_8XY1() {
        V[Vx] = V[Vx] | V[Vy];
        incProgramCount();
    }
    // AND Vx, Vy - Set Vx = Vx AND Vy.
    private void instrucao_8XY2() {
        V[Vx] = V[Vx] & V[Vy];
        incProgramCount();
    }
    // XOR Vx, Vy - Set Vx = Vx XOR Vy.
    private void instrucao_8XY3() {
        V[Vx] = V[Vx] ^ V[Vy];
        incProgramCount();
    }
    // ADD Vx, Vy - Set Vx = Vx + Vy, set VF = carry.
    private void instrucao_8XY4() {
        int sum = V[Vx] + V[Vy];
        if(sum >= 256){
            V[0xf] = 1;
            sum -= 256;
        }else{
            V[0xf] = 0;
        }
       V[Vx] = sum;
        incProgramCount();
    }
    // SUB Vx, Vy - Set Vx = Vx - Vy, set VF = NOT borrow.
    private void instrucao_8XY5() {
        V[0xf] = V[Vx] > V[Vy] ? 1 : 0;
        int sub = V[Vx] - V[Vy];
        if(V[Vx] > V[Vy])
            sub -= 256;
        V[Vx] = sub;
        incProgramCount();
    }
    // SHR Vx {, Vy} - Set Vx = Vx SHR 1.
    private void instrucao_8XY6() {
        V[0xf] = (V[Vx] & 0x1);
        V[Vx] >>= 1; // divisão por dois chique
        incProgramCount();
    }
    // SUBN Vx, Vy - Set Vx = Vy - Vx, set VF = NOT borrow.
    private void instrucao_8XY7() {
        int sub=0;
        if(V[Vy] > V[Vx]){
            V[0xf] = 1;
            sub = V[Vy] - V[Vx];
        }else{
            V[0xf] = 0;
            sub = 256 + V[Vy] - V[Vx];
        }
        V[Vx] = sub;
        incProgramCount();
    }
    // SHL Vx {, Vy} - Set Vx = Vx SHL 1.
    private void instrucao_8XYE() {
        V[0xf] = (V[Vx] & 0x7) == 1 ? 1: 0;
        V[Vx] <<= 1; // multiplicação por dois chique
        incProgramCount();
    }
    // SNE Vx, Vy - Skip next instruction if Vx != Vy.
    private void instrucao_9XY0() {
        if(V[Vx] != V[Vy])
            SkipNextInstrucao();
        else
            incProgramCount();
    }
    // LD I, addr - Set I = nnn.
    private void instrucao_ANNN() {
        this.I = nnn;
        incProgramCount();
    }
    // JP V0, addr - Jump to location nnn + V0.
    private void instrucao_BNNN() {
        this.PC = nnn + V[0];
    }
    // RND Vx, byte - Set Vx = random byte AND kk.
    private void instrucao_CXKK() {
        V[Vx] = random.nextInt(256) & kk;
        incProgramCount();
    }
    // DRW Vx, Vy, nibble - Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
    private void instrucao_DXYN() {
        int height = OpCode & 0x000F;
        V[0xf] = 0;
        try{
        for (int yLine = 0; yLine < height; yLine++) {
            int pixel = this.memory[I + yLine];
            for (int xLine = 0; xLine < 8; xLine++) {
                // check each bit (pixel) in the 8 bit row
	        if ((pixel & (0x80 >> xLine)) != 0) {
                    int xCoord = V[Vx]+xLine;
                    int yCoord = V[Vy]+yLine;
                    if(xCoord < 64 && yCoord < 32){
                        if(screen.getPixel(xCoord, yCoord) == 1){
                            V[0xf] = 1;
                        }
                    }
                    screen.setPixel(xCoord, yCoord);
                }
            }
        }
        }catch(java.lang.ArrayIndexOutOfBoundsException e){}
        screen.setDrawFlag(true);
        incProgramCount();
    }
    // SKP Vx - Skip next instruction if key with the value of Vx is pressed.
    private void instrucao_EX9E(){
        if(teclado.isPressed(V[Vx])) 
            SkipNextInstrucao();
        else
            incProgramCount();
    }
    // SKNP Vx - Skip next instruction if key with the value of Vx is not pressed.
    private void instrucao_EXA1() {
        if(!teclado.isPressed(V[Vx])) 
            SkipNextInstrucao();
        else
            incProgramCount();
    }
    // LD Vx, DT - Set Vx = delay timer value.
    private void instrucao_FX07() {
        V[Vx] = delay_timer;
        incProgramCount();
    }
    // LD Vx, K - Wait for a key press, store the value of the key in Vx
    private void instrucao_FX0A() {
        boolean keypress = false;
        for(int k=0;k<16;k++){
            if(teclado.isPressed(k)){
                keypress = true;
                V[Vx] = k;
            }
        }     
        if(!keypress) return;
        
        incProgramCount();
    }
    // LD DT, Vx - Set delay timer = Vx.
    private void instrucao_FX15() {
        delay_timer = V[Vx];
        incProgramCount();
    }
    // LD ST, Vx - Set sound timer = Vx.
    private void instrucao_FX18() {
        sound_timer = V[Vx];
        incProgramCount();
    }
    // ADD I, Vx - Set I = I + Vx.
    private void instrucao_FX1E() {
        this.I += V[Vx];
        incProgramCount();
    }
    // LD F, Vx - Set I = location of sprite for digit Vx.
    private void instrucao_FX29() {
        this.I = V[Vx] * 5;
        incProgramCount();
    }
    // LD B, Vx - Store BCD representation of Vx in memory locations I, I+1, and I+2.
    private void instrucao_FX33() {
        int num = V[Vx];
        memory[I] = num / 100;
        memory[I+1] = (num % 100) / 10;
        memory[I+2] = (num % 100) % 10;
        /*for(int i=3;i>0;i--){
            this.memory[this.I + i - 1] = num % 10;
            num /= 10;
        }*/
        incProgramCount();
    }
    // LD [I], Vx - Store registers V0 through Vx in memory starting at location I.
    private void instrucao_FX55() {
        //System.arraycopy(V, 0, this.memory, this.I, Vx);
        for(int idx=0;idx<=Vx;idx++){
            this.memory[I + idx] = V[idx];
        }
        incProgramCount();
    }
    // LD Vx, [I] - Read registers V0 through Vx from memory starting at location I.
    private void instrucao_FX65() {
        //System.arraycopy(this.memory, this.I, this.V, 0, Vx);
        for(int idx=0;idx<=Vx;idx++){
            V[idx] = this.memory[I + idx];
        }
        incProgramCount();
    }
    
    public void debug(){
        LOGGER.log(Level.INFO, "\nciclos\t OpCode\t PC\t I\t SP\t Regs.\t\n{20}\t {0}\t  {1}\t {2}\t {19}\t {3} {4} {5} {6} {7} {8} {9} {10} {11} {12} {13} {14} {15} {16} {17} {18}", new Object[]{
        Integer.toHexString(OpCode).toUpperCase(), Integer.toHexString(PC).toUpperCase(), Integer.toHexString(I).toUpperCase(), 
        stack[0],stack[1],stack[2],stack[3],stack[4],stack[5],stack[6],stack[7],stack[8],stack[9],stack[10],stack[11],stack[12],stack[13],stack[14],stack[15], SP, ciclos++});
    }
}
