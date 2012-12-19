/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.common;

import java.io.File;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import pt.org.aguiaj.core.commands.ChangeWorkingDirCommand;
import pt.org.aguiaj.core.interpreter.Instruction;
import pt.org.aguiaj.core.interpreter.ParseException;
import pt.org.aguiaj.core.interpreter.Parser;

public class DragNDrop {

	public static DropTargetListener createDropListener() {
		return new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
			}

			public void dropAccept(DropTargetEvent event) {			
			}

			public void drop(DropTargetEvent event) {
				String[] data = (String[]) event.data;
				if(data.length == 1) {
					IPath path = new Path(data[0]);
					File file = path.toFile();

					if(file.exists() && file.isDirectory()) {
						ChangeWorkingDirCommand command = new ChangeWorkingDirCommand();
						command.setDirectory(path);
						//						ApplicationActionBarAdvisor.resetPerspectiveAction.run();
						try {
							command.execute(null);
						} catch (ExecutionException e) {						
							e.printStackTrace();
						}
					}
				}
			}
		};
	}

	public static void addFileDragNDropSupport(Control control) {
		Transfer[] types = new Transfer[] { FileTransfer.getInstance() };

		DropTarget dropTarget = new DropTarget(control, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE);
		dropTarget.setTransfer(types);

		dropTarget.addDropListener(createDropListener());
	}

	public static void addFileDragNDropSupportObjectArea(Composite area) {
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		final TextTransfer textTransfer = TextTransfer.getInstance();

		Transfer[] types = new Transfer[] { fileTransfer, textTransfer };

		DropTarget dropTarget = new DropTarget(area, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE);
		dropTarget.setTransfer(types);

		dropTarget.addDropListener(new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
			}

			public void dropAccept(DropTargetEvent event) {			
			}

			public void drop(DropTargetEvent event) {

				if(fileTransfer.isSupportedType(event.currentDataType)) {
					String[] data = (String[]) event.data;
					if(data.length == 1) {
						IPath path = new Path(data[0]);
						File file = path.toFile();

						if(file.exists() && !file.isDirectory() && path.getFileExtension().matches("jpg|jpeg|png|gif|bmp")) {
							try {
								Instruction instruction = 
									Parser.accept("ImageUtils.fromFile(\"" + file.getAbsolutePath() + "\")");
								
								if(instruction != null)
									instruction.getCommand().execute();
							}
							catch(ParseException e) {

							}
						}
					}
				}
//				else if(textTransfer.isSupportedType(event.currentDataType)) {
//					String data = (String) event.data;
//					Instruction instruction = Parser.accept(data);
//					
//					if(instruction != null) {
//						instruction.getCommand().execute();
//					}
//					else {
//						TextBuffer text = new TextBuffer(data);
//						ObjectsView.getInstance().addObjectWidget(text, ObjectModelRefactor.getInstance().nextReference(TextBuffer.class), TextBuffer.class);					
//					}
//				}
			}
		});
	}


}
