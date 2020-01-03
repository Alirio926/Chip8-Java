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

import java.awt.event.KeyEvent;

/**
 *
 * @author Alirio Oliveira <https://github.com/Alirio926>
 */

public class Teclado {
    private boolean[] keys;
    
    public Teclado(){
        keys = new boolean[16];
        for(int i = 0; i < 16; i++)
            keys[i] = false;
    }
    public void doTeclado(){
        for(int i = 0; i < 16; i++)
            keys[i] = false;
    }
    public void KeyUp(KeyEvent key){
        switch(key.getKeyCode()){
            case KeyEvent.VK_1:{ keys[0] = false; } break;
            case KeyEvent.VK_2:{ keys[1] = false; } break;
            case KeyEvent.VK_3:{ keys[2] = false; } break;
            case KeyEvent.VK_4:{ keys[3] = false; } break;
            
            case KeyEvent.VK_Q:{ keys[4] = false; } break;
            case KeyEvent.VK_W:{ keys[5] = false; } break;
            case KeyEvent.VK_E:{ keys[6] = false; } break;
            case KeyEvent.VK_R:{ keys[7] = false; } break;
            
            case KeyEvent.VK_A:{ keys[8] = false; } break;
            case KeyEvent.VK_S:{ keys[9] = false; } break;
            case KeyEvent.VK_D:{ keys[10] = false; } break;
            case KeyEvent.VK_F:{ keys[11] = false; } break;
            
            case KeyEvent.VK_Z:{ keys[12] = false; } break;
            case KeyEvent.VK_X:{ keys[13] = false; } break;
            case KeyEvent.VK_C:{ keys[14] = false; } break;
            case KeyEvent.VK_V:{ keys[15] = false; } break;
            
            default: System.out.println("UpKey: "+key.getKeyCode());
        }
    }
    public void KeyDown(KeyEvent key){
        switch(key.getKeyCode()){
            case KeyEvent.VK_1:{ keys[0] = true; } break;
            case KeyEvent.VK_2:{ keys[1] = true; } break;
            case KeyEvent.VK_3:{ keys[2] = true; } break;
            case KeyEvent.VK_4:{ keys[3] = true; } break;
            
            case KeyEvent.VK_Q:{ keys[4] = true; } break;
            case KeyEvent.VK_W:{ keys[5] = true; } break;
            case KeyEvent.VK_E:{ keys[6] = true; } break;
            case KeyEvent.VK_R:{ keys[7] = true; } break;
            
            case KeyEvent.VK_A:{ keys[8] = true; } break;
            case KeyEvent.VK_S:{ keys[9] = true; } break;
            case KeyEvent.VK_D:{ keys[10] = true; } break;
            case KeyEvent.VK_F:{ keys[11] = true; } break;
            
            case KeyEvent.VK_Z:{ keys[12] = true; } break;
            case KeyEvent.VK_X:{ keys[13] = true; } break;
            case KeyEvent.VK_C:{ keys[14] = true; } break;
            case KeyEvent.VK_V:{ keys[15] = true; } break;
            
            default: System.out.println("DownKey: "+key.getKeyCode());
        }
    }
    public boolean isPressed(int k){ 
        return keys[k]; 
    
    }
}
