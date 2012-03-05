/*
 * Autopsy Forensic Browser
 * 
 * Copyright 2011 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 * 
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

package org.sleuthkit.autopsy.ingest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;
import org.sleuthkit.autopsy.casemodule.Case;

/**
 * Toolbar for Ingest
 * 
 */
public class IngestMessagesToolbar extends javax.swing.JPanel {

    /** Creates new form IngestMessagesToolbar */
    public IngestMessagesToolbar() {
        initComponents();
        customizeComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ingestMessagesButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(32767, 25));
        setPreferredSize(new java.awt.Dimension(80, 23));

        ingestMessagesButton.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        ingestMessagesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/ingest/eye-icon-16.png"))); // NOI18N
        ingestMessagesButton.setText(org.openide.util.NbBundle.getMessage(IngestMessagesToolbar.class, "IngestMessagesToolbar.ingestMessagesButton.text")); // NOI18N
        ingestMessagesButton.setToolTipText(org.openide.util.NbBundle.getMessage(IngestMessagesToolbar.class, "IngestMessagesToolbar.ingestMessagesButton.toolTipText")); // NOI18N
        ingestMessagesButton.setEnabled(false);
        ingestMessagesButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ingestMessagesButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ingestMessagesButton.setMargin(new java.awt.Insets(2, 0, 2, 2));
        ingestMessagesButton.setMaximumSize(new java.awt.Dimension(65, 23));
        ingestMessagesButton.setMinimumSize(new java.awt.Dimension(65, 23));
        ingestMessagesButton.setPreferredSize(new java.awt.Dimension(65, 23));
        ingestMessagesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ingestMessagesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ingestMessagesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ingestMessagesButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void ingestMessagesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ingestMessagesButtonActionPerformed
       showIngestMessages();
    }//GEN-LAST:event_ingestMessagesButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ingestMessagesButton;
    // End of variables declaration//GEN-END:variables


    private void customizeComponents() {
        
        IngestMessagePanel.addPropertyChangeSupportListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                final int numberMessages = (Integer)evt.getNewValue();
                ingestMessagesButton.setText(Integer.toString(numberMessages));
            }
            
        });
        
        Case.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(Case.CASE_CURRENT_CASE))
                    setEnabled(evt.getNewValue() != null);
            }
            
        });
    }

    private void showIngestMessages() {
        IngestMessageTopComponent tc = IngestMessageTopComponent.findInstance();
       
        Mode mode = WindowManager.getDefault().findMode("floatingLeftBottom");
        if (mode != null) {
            //TopComponent[] tcs = mode.getTopComponents();
            mode.dockInto(tc);
            tc.open();
            //tc.requestActive();   
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        ingestMessagesButton.setEnabled(enabled);
    }
}
