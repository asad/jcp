/* 
 * Copyright (C) 2009 Konstantin Tokarev <annulen@users.sourceforge.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.jchempaint.controller;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.jchempaint.controller.RotateModule;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoFactory;
import org.openscience.jchempaint.controller.undoredo.IUndoRedoable;
import org.openscience.jchempaint.controller.undoredo.UndoRedoHandler;
import org.openscience.jchempaint.renderer.BoundsCalculator;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.interfaces.IAtomContainer;


public class Rotate3DModule extends RotateModule {

    private double rotationAnglePhi;
    private double rotationAnglePsi;
    private boolean horizontalFlip;
    private boolean verticalFlip;

    /**
     * Constructor 
     * @param chemModelRelay
     */
    public Rotate3DModule(IChemModelRelay chemModelRelay) {
        super(chemModelRelay);
        logger.debug("constructor");
        horizontalFlip = false;
        verticalFlip = false;
    }
    
    /**
     * On mouse drag, quasi-3D rotation around the center is done
     * (It isn't real 3D rotation because of truncation of transformation
     * matrix to 2x2)
     */
    @Override
    public void mouseDrag(Point2d worldCoordFrom, Point2d worldCoordTo) {

        if (selectionMade) {
            rotationPerformed=true;

            final int SLOW_DOWN_FACTOR=1;
            rotationAnglePhi += (worldCoordTo.x - worldCoordFrom.x)/SLOW_DOWN_FACTOR;
            rotationAnglePsi += (worldCoordTo.y - worldCoordFrom.y)/SLOW_DOWN_FACTOR;

            /* For more info on the mathematics, see Wiki at 
             * http://en.wikipedia.org/wiki/Rotation_matrix
             */
            double cosinePhi = java.lang.Math.cos(rotationAnglePhi);
            double sinePhi = java.lang.Math.sin(rotationAnglePhi);
            double cosinePsi = java.lang.Math.cos(rotationAnglePsi);
            double sinePsi = java.lang.Math.sin(rotationAnglePsi);
            
            for (int i = 0; i < startCoordsRelativeToRotationCenter.length; i++) {
                double newX = startCoordsRelativeToRotationCenter[i].x*cosinePhi
                        + startCoordsRelativeToRotationCenter[i].y*sinePhi*sinePsi; 
                double newY = startCoordsRelativeToRotationCenter[i].y*cosinePsi;

                Point2d newCoords = new Point2d(newX + rotationCenter.x, newY
                        + rotationCenter.y);

                selection.getConnectedAtomContainer().getAtom(i).setPoint2d(
                        newCoords);
            }

            if ((cosinePhi < 0) && (!horizontalFlip)) {
                horizontalFlip = true;
                chemModelRelay.invertStereoInSelection();
            }
            if ((cosinePhi >= 0) && (horizontalFlip)) {
                horizontalFlip = false;
                chemModelRelay.invertStereoInSelection();
            }
            if ((cosinePsi < 0) && (!verticalFlip)) {
                verticalFlip = true;
                chemModelRelay.invertStereoInSelection();
            }
            if ((cosinePsi >= 0) && (verticalFlip)) {
                verticalFlip = false;
                chemModelRelay.invertStereoInSelection();
            }
        }
        chemModelRelay.updateView();
    }

   /* public void invertStereoInSelection() {
        IAtomContainer toflip;
        RendererModel renderModel = renderer.getRenderer2DModel();
        if (renderModel.getSelection().getConnectedAtomContainer()!=null &&
            renderModel.getSelection().getConnectedAtomContainer().getAtomCount()!=0   ) {
            toflip = renderModel.getSelection().getConnectedAtomContainer();
        } else
            return;
            
        for(IBond bond : toflip.bonds()){
            if(bond.getStereo()==IBond.Stereo.UP)
                bond.setStereo(IBond.Stereo.DOWN);
            else if(bond.getStereo()==IBond.Stereo.DOWN)
                bond.setStereo(IBond.Stereo.UP);
            else if(bond.getStereo()==IBond.Stereo.UP_INVERTED)
                bond.setStereo(IBond.Stereo.DOWN_INVERTED);
            else if(bond.getStereo()==IBond.Stereo.DOWN_INVERTED)
                bond.setStereo(IBond.Stereo.UP_INVERTED);
        }
    }*/
    
    public String getDrawModeString() {
        return "Rotate in space";
    }
}
