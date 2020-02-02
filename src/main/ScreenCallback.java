/*
 * Copyright (C) 2020 Alirio Oliveira
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

/**
 *
 * @author Alirio Oliveira
 */
public interface ScreenCallback {
    public void clearScreen();
    public int getScreenPixel(int x, int y);
    public void setScreenPixel(int x, int y);
    public boolean getScreenDrawFlag();
    public void setScreenDrawFlag(boolean drawFlag);    
}
