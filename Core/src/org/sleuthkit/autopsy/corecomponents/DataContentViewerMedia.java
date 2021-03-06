/*
 * Autopsy Forensic Browser
 *
 * Copyright 2011-2014 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *s
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.corecomponents;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import org.sleuthkit.autopsy.corecomponentinterfaces.DataContentViewer;
import org.sleuthkit.autopsy.coreutils.ImageUtils;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.AbstractFile.MimeMatchEnum;
import org.sleuthkit.datamodel.TskData.TSK_FS_NAME_FLAG_ENUM;

/**
 * Media content viewer for videos, sounds and images.
 */
@ServiceProviders(value = {
    @ServiceProvider(service = DataContentViewer.class, position = 5)
})
public class DataContentViewerMedia extends javax.swing.JPanel implements DataContentViewer {

    private static final Set<String> AUDIO_EXTENSIONS = new TreeSet<>(Arrays.asList(".mp3", ".wav", ".wma")); //NON-NLS
    private static final Logger logger = Logger.getLogger(DataContentViewerMedia.class.getName());
    private AbstractFile lastFile;
    //UI
    private final MediaViewVideoPanel videoPanel;
    private final SortedSet<String> videoExtensions; // get them from the panel
    private final SortedSet<String> imageExtensions; 
    private final SortedSet<String> videoMimes;
    private final SortedSet<String> imageMimes;
    private final MediaViewImagePanel imagePanel;
    private boolean videoPanelInited;
    private boolean imagePanelInited;
    private static final String IMAGE_VIEWER_LAYER = "IMAGE"; //NON-NLS
    private static final String VIDEO_VIEWER_LAYER = "VIDEO"; //NON-NLS

    /**
     * Creates new form DataContentViewerVideo
     */
    public DataContentViewerMedia() {

        initComponents();

        // get the right panel for our platform
        videoPanel = MediaViewVideoPanel.createVideoPanel();
        videoPanelInited = videoPanel.isInited();
        videoExtensions = new TreeSet<>(Arrays.asList(videoPanel.getExtensions()));
        videoMimes = new TreeSet<>(videoPanel.getMimeTypes());

        imagePanel = new MediaViewImagePanel();
        imagePanelInited = imagePanel.isInited();
        imageMimes = new TreeSet<>(imagePanel.getMimeTypes());
        imageExtensions = new TreeSet<>(imagePanel.getExtensions());
        
        customizeComponents();
        logger.log(Level.INFO, "Created MediaView instance: " + this); //NON-NLS
    }

    private void customizeComponents() {
        add(imagePanel, IMAGE_VIEWER_LAYER);
        add(videoPanel, VIDEO_VIEWER_LAYER);

        showVideoPanel(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.CardLayout());
        getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(DataContentViewerMedia.class, "DataContentViewerMedia.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void setNode(Node selectedNode) {
        try {
            if (selectedNode == null) {
                resetComponent();
                return;
            }

            AbstractFile file = selectedNode.getLookup().lookup(AbstractFile.class);
            if (file == null) {
                resetComponent();
                return;
            }

            if (file.equals(lastFile)) {
                return; //prevent from loading twice if setNode() called mult. times
            }

            lastFile = file;

            final Dimension dims = DataContentViewerMedia.this.getSize();
            //logger.info("setting node on media viewer"); //NON-NLS
            if (imagePanelInited && isImageSupported(file)) {
                imagePanel.showImageFx(file, dims);
                this.showVideoPanel(false);
            } else if (videoPanelInited && isVideoSupported(file)) {
                videoPanel.setupVideo(file, dims);
                this.showVideoPanel(true);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception while setting node", e); //NON-NLS
        }
    }

    /**
     * switch to visible video or image panel
     *
     * @param showVideo true if video panel, false if image panel
     */
    private void showVideoPanel(boolean showVideo) {
        CardLayout layout = (CardLayout) this.getLayout();
        if (showVideo) {
            layout.show(this, VIDEO_VIEWER_LAYER);
        } else {
            layout.show(this, IMAGE_VIEWER_LAYER);
        }
    }

    @Override
    public String getTitle() {
        return NbBundle.getMessage(this.getClass(), "DataContentViewerMedia.title");
    }

    @Override
    public String getToolTip() {
        return NbBundle.getMessage(this.getClass(), "DataContentViewerMedia.toolTip");
    }

    @Override
    public DataContentViewer createInstance() {
        return new DataContentViewerMedia();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void resetComponent() {
        videoPanel.reset();
        imagePanel.reset();
        lastFile = null;
    }

    /**
     * 
     * @param file
     * @return True if a video file that can be displayed 
     */
    private boolean isVideoSupported(AbstractFile file) {
        String name = file.getName().toLowerCase();
        
        if ((containsExt(name, AUDIO_EXTENSIONS) || containsExt(name, videoExtensions)) && 
                (!videoMimes.isEmpty() && file.isMimeType(videoMimes) == MimeMatchEnum.TRUE)) {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param file
     * @return  True if an image file that can be displayed
     */
    private boolean isImageSupported(AbstractFile file) {
        String name = file.getName().toLowerCase();
        
        // blackboard
        if (!imageMimes.isEmpty()) {
            MimeMatchEnum mimeMatch = file.isMimeType(imageMimes);
            if (mimeMatch == MimeMatchEnum.TRUE) {
                return true;
            }
            else if (mimeMatch == MimeMatchEnum.FALSE) {
                return false;
            }
        }
        
        // extension
        if (containsExt(name, imageExtensions)) {
            return true;
        }
        // our own signature checks for important types
        else if (ImageUtils.isJpegFileHeader(file)) {
            return true;
        }
        else if (ImageUtils.isPngFileHeader(file)) {
            return true;
        }
        
        //for gstreamer formats, check if initialized first, then
        //support audio formats, and video formats
        return false;
    }
    
    @Override
    public boolean isSupported(Node node) {
        if (node == null) {
            return false;
        }

        AbstractFile file = node.getLookup().lookup(AbstractFile.class);
        if (file == null) {
            return false;
        }

        if (file.getSize() == 0) {
            return false;
        }
        
        if (imagePanelInited) {
            if (isImageSupported(file))
                return true;
        } 
        
        if (videoPanelInited && videoPanel.isInited()) {
            if (isVideoSupported(file))
                return true;
        }

        return false;
    }

    @Override
    public int isPreferred(Node node) {
        //special case, check if deleted video, then do not make it preferred
        AbstractFile file = node.getLookup().lookup(AbstractFile.class);
        if (file == null) {
            return 0;
        }
        String name = file.getName().toLowerCase();
        boolean deleted = file.isDirNameFlagSet(TSK_FS_NAME_FLAG_ENUM.UNALLOC);

        if (containsExt(name, videoExtensions) && deleted) {
            return 0;
        } 
        else {
            return 7;
        }
        
    }

    private static boolean containsExt(String name, Set<String> exts) {
        int extStart = name.lastIndexOf(".");
        String ext = "";
        if (extStart != -1) {
            ext = name.substring(extStart, name.length()).toLowerCase();
        }
        return exts.contains(ext);
    }
}
