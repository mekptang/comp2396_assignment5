import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import javax.swing.JComboBox;


/**
 * @author BenTang
 *The image puzzle game
 *Allowed to import user's .jpg photo for playing game
 */
@SuppressWarnings({ "unused", "serial" })
public class JavaPuzzle extends JFrame {
	
	/**
	 * Button for load image from user
	 */
	public static JButton loadButton = new JButton("Load Another Image");
	/**
	 * Text area for prompt information to user
	 */
	public static JTextArea textArea = new JTextArea();
	/**
	 * Scrollpane for contain the textarea
	 */
	public static JScrollPane scrollPane = new JScrollPane();
	/**
	 * The game window itself
	 */
	public static JavaPuzzle frame;
	/**
	 * List for containing the correct position of cropped image
	 */
	private static ArrayList<String> hitList;
	/**
	 * Mouse selected object
	 */
	String select1;
	/**
	 * Mouse swapped object
	 */
	String select2;
	
	private JPanel contentPane;
	private JLabel lb1;
	private JLabel lb2;

	//Constant
	private static final int DIMENSION = 800;
	private static final int SLICE = 100;
	private static final int PIECE_DIMENSION = (int) (DIMENSION / Math.sqrt(SLICE));
	private static final int COUNT_WIDTH = (int) Math.sqrt(SLICE);
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					loadButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							//System.out.println("load other image.");
							frame.dispose();
							
							frame = new JavaPuzzle();
							frame.setTitle("Puzzle Image");
							frame.setResizable(false);
							frame.setVisible(true);
						}
					});
					
					frame = new JavaPuzzle();
					frame.setTitle("Puzzle Image");
					frame.setResizable(false);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				
				
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JavaPuzzle() {
		hitList = new ArrayList<String>();
		ImageFilter filter = new ImageFilter();
		 JFileChooser jfc;
		 int returnValue;
		 jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		 returnValue = jfc.showOpenDialog(null);
		
		if (returnValue == JFileChooser.APPROVE_OPTION && filter.accept(jfc.getSelectedFile())) {
			File selectedFile = jfc.getSelectedFile();
			initComponents(selectedFile.getAbsolutePath());
			scrollPane.setViewportView(textArea);
			textArea.setEditable(false);
			textArea.setText("Game started!\n");
		}else if(jfc.getSelectedFile()==null){
			System.exit(0);
		}else {
			JOptionPane.showMessageDialog(null, filter.getDescription());
			System.exit(0);
		}
	}

	/**
	 * @param imgPath user's photo
	 * 
	 * All the initial elements for the game board
	 */
	private void initComponents(String imgPath) {
		BufferedImage img = null;
		JButton showButton = new JButton("Show Original Image");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 1000);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		
		//get image
		try 
		{
		    img = resize(ImageIO.read(new File(imgPath)), 800, 800);
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(null, ".jpg file can be used only");
			System.exit(0);
		}
		
		ArrayList<BufferedImage> pss = new ArrayList<BufferedImage>();
		
			for(int i= 0; i < COUNT_WIDTH; i++) {
				for(int j = 0; j < COUNT_WIDTH; j++) {
					BufferedImage temp = img.getSubimage(i*PIECE_DIMENSION, j*PIECE_DIMENSION, PIECE_DIMENSION, PIECE_DIMENSION);
					pss.add(temp);
				}
			}
		
		ArrayList<Integer> index = new ArrayList<Integer>();
		
		for(int i = 0; i < SLICE; i++) {
			index.add(i);
		}
		
		Collections.shuffle(index);
		
		for(int i= 0; i < COUNT_WIDTH; i++) {
			for(int j = 0; j < COUNT_WIDTH; j++) {
				BufferedImage temp = pss.get(index.get(i*COUNT_WIDTH+j));
				
				JLabel lbl1 = new JLabel(String.valueOf(i+j));
				panel.add(lbl1);
				lbl1.setLocation(i*PIECE_DIMENSION, j*PIECE_DIMENSION);
				lbl1.setSize(PIECE_DIMENSION, PIECE_DIMENSION);
				ImageIcon icon = new ImageIcon(temp); 
				lbl1.setIcon(icon);
				lbl1.setText(index.get(i*COUNT_WIDTH+j).toString());
				
				lbl1.addMouseListener(new MouseAdapter()  
				{
					/* (non-Javadoc)
					 * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
					 */
					public void mouseEntered(MouseEvent e) {
						 Border border = BorderFactory.createLineBorder(Color.YELLOW, 4);
				    	 lbl1.setBorder(border);
				    	 lb2 = (JLabel) e.getSource();
				    	 select2 = lb2.getText();
			        }
					
					/* (non-Javadoc)
					 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
					 */
					public void mousePressed(MouseEvent e) {
						Border border = BorderFactory.createLineBorder(Color.YELLOW, 4);
				    	 lbl1.setBorder(border);
				    	 lb1 = (JLabel) e.getSource();
				    	 select1 = lb1.getText();
				    	 //System.out.println(select1);
					}
					
					/* (non-Javadoc)
					 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
					 */
					public void mouseExited(MouseEvent e) {
						if(lb1 != (JLabel)e.getSource()) {
							Border empty = BorderFactory.createEmptyBorder();
					    	lbl1.setBorder(empty);
						}
			        }
					
				    /* (non-Javadoc)
				     * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
				     */
				    public void mouseReleased(MouseEvent e) {
				    	
				    	Border empty = BorderFactory.createEmptyBorder();
				    	lbl1.setBorder(empty);
				    	 if(!hitList.contains(lb2.getText()) && !hitList.contains(lb1.getText()) && select1 != null && select2 != null && (select1 != select2)) {
				    		 //System.out.println("swap: " + select1 + " " + select2);
				    		 //textArea.append("swap: " + select1 + " " + select2 + "\n");
				    		 
				    		 Icon icon = new ImageIcon(temp);
				    		 icon = lb1.getIcon();
				    		 lb1.setIcon(lb2.getIcon());
				    		 lb2.setIcon(icon);
				    		 lb1.setText(select2);
				    		 lb2.setText(select1);
				    		 //Collections.swap(index, Integer.parseInt(select1), Integer.parseInt(select2));
				    		 Integer temp = Integer.parseInt(select1);
				    		 Integer s2 = index.indexOf(Integer.parseInt(select2));
				    		 index.set(index.indexOf(Integer.parseInt(select1)), Integer.parseInt(select2));
				    		 index.set(s2, temp);

				    		 temp = Integer.parseInt(select1);
				    		 Integer temp2 = (Integer)index.indexOf(Integer.parseInt(select1));
				    		 //textArea.append("suppose: " + temp.toString());
				    		 //textArea.append("now: " + temp2.toString() + "\n");
				    	 	 if(temp == temp2) {
				    	 		 hitList.add(lb2.getText());
				    	 		 //System.out.println(hitList);
				    	 		textArea.append("Image block in correct position.\n");
				    	 	 }
				    	 }
				    	 
				    	 select1 = null;
				    	 select2 = null;
				    	 
				    	 if(isCollectionSorted(index)) {
				    		 JOptionPane.showMessageDialog(null, "You win!");
				    		 textArea.append("You win!\n");
				    		 showButton.doClick();
				    	 }
				    } 
				});
				
				
			}
		}
		
		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		showButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Collections.sort(index);
				textArea.append("-------------------------------------GAME OVER-------------------------------------\n");
				textArea.append("Please load another image for restart!\n");
				panel.removeAll();
				for(int i= 0; i < COUNT_WIDTH; i++) {
					for(int j = 0; j < COUNT_WIDTH; j++) {
						BufferedImage temp = pss.get(index.get(i*COUNT_WIDTH+j));
						JLabel lbl1 = new JLabel(String.valueOf(i+j));
						panel.add(lbl1);
						lbl1.setLocation(i*PIECE_DIMENSION, j*PIECE_DIMENSION);
						lbl1.setSize(PIECE_DIMENSION, PIECE_DIMENSION);
						ImageIcon icon = new ImageIcon(temp); 
						lbl1.setIcon(icon);
						lbl1.setText(index.get(i*COUNT_WIDTH+j).toString());
						}
					}
			}
		});
		
		//Plane layout
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(27)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, DIMENSION, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(58, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addGap(64)
					.addComponent(loadButton)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 387, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(showButton)
							.addGap(200)
							.addComponent(exitButton)
							.addGap(200))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, DIMENSION, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(exitButton)
						.addComponent(showButton)
						.addComponent(loadButton))
					.addContainerGap())
		);
		
		panel.setLayout(null);
		contentPane.setLayout(gl_contentPane);
		
	}
	
	/**
	 * @param list The order list of image
	 * @return boolean for check the list whether sorted or not
	 */
	private boolean isCollectionSorted(ArrayList<Integer> list) {
	    ArrayList<Integer> copy = new ArrayList<Integer>(list);
	    Collections.sort(copy);
	    return copy.equals(list);
	}
	
	/**
	 * @author BenTang
	 *Find the extension of the user input file
	 */
	private static class Utils {

	    public final static String jpeg = "jpeg";
	    public final static String jpg = "jpg";
	    public final static String gif = "gif";
	    public final static String tiff = "tiff";
	    public final static String tif = "tif";
	    public final static String png = "png";

	    public static String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }
	}
	
	/**
	 * @author BenTang
	 *Check the input file whether a image file
	 */
	private class ImageFilter extends FileFilter {
		 
	    //Accept all directories and all gif, jpg, tiff, or png files.
	    public boolean accept(File f) {
	        if (f.isDirectory()) {
	            return true;
	        }
	 
	        String extension = Utils.getExtension(f);
	        if (extension != null) {
	            if (extension.equals(Utils.tiff) ||
	                extension.equals(Utils.tif) ||
	                extension.equals(Utils.gif) ||
	                extension.equals(Utils.jpeg) ||
	                extension.equals(Utils.jpg)||
	                extension.equals(Utils.png)) {
	                    return true;
	            } else {
	                return false;
	            }
	        }
	 
	        return false;
	    }
	 
	    //The description of this filter
	    public String getDescription() {
	        return "Just accept image file";
	    }
	}
	
	/**
	 * @param img Image which need to crop
	 * @param newW The new width for the image
	 * @param newH The new height for the image
	 * @return The cropped image
	 */
	private static BufferedImage resize(BufferedImage img, int newW, int newH) {
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}
	
}
