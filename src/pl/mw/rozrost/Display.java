package pl.mw.rozrost;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;


public class Display extends JFrame {

	private static final long serialVersionUID = 1L;
	private Plansza panel_1 = null;
	private JPanel contentPane;
	private String[] pedzle = new String[] {"Moore", "von Nauman", "Hexagonal left", "Hexagonal right", "Hexagonal random", "Pentagonal left", "Pentagonal right", "Pentagonal top", "Pentagonal bottom", "Pentagonal random"};
	private JCheckBoxMenuItem chckbxmntmPamitajRozmieszczenieZiaren;
	private JCheckBoxMenuItem chckbxmntmCzyPlanszePrzed;
	
	public Display() {
		System.out.println("Display::Display();");
		setForeground(UIManager.getColor("Button.highlight"));
		setBackground(Color.WHITE);
		setTitle("Modelowanie Wielkoskalowe - Rozrost ziaren");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 960, 600);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnPlik = new JMenu("Plik");
		menuBar.add(mnPlik);
		
		JMenuItem mntmZamknij = new JMenuItem("Zamknij");
		mntmZamknij.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.exit(0);
			}
		});
		
		chckbxmntmPamitajRozmieszczenieZiaren = new JCheckBoxMenuItem("Pamiętaj rozmieszczenie ziaren");
		chckbxmntmPamitajRozmieszczenieZiaren.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Core.Config.menuSaveMap = !Display.this.chckbxmntmPamitajRozmieszczenieZiaren.isSelected();
			}
		});
		
		chckbxmntmCzyPlanszePrzed = new JCheckBoxMenuItem("Czyść plansze przed startem");
		chckbxmntmCzyPlanszePrzed.setSelected(true);
		chckbxmntmCzyPlanszePrzed.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Core.Config.menuClean = !Display.this.chckbxmntmCzyPlanszePrzed.isSelected();
			}
		});
		mnPlik.add(chckbxmntmCzyPlanszePrzed);
		mnPlik.add(chckbxmntmPamitajRozmieszczenieZiaren);
		mnPlik.add(mntmZamknij);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		panel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("KeyPressed: "+e.getKeyCode()+", ts="+e.getWhen());
			}
			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println("keyReleased: "+e.getKeyCode()+", ts="+e.getWhen());
			}
		});
		
			// =================================	pasek	================================
		
		JLabel lblWarunkiBrzegowe = new JLabel("Periodyczno\u015B\u0107: ");
		panel.add(lblWarunkiBrzegowe);

		final JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("Warunki Brzegowe");
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				Core.Config.bc = comboBox.getSelectedIndex();
				//System.out.println("Wybrano bc: "+Core.Config.bc);
			}
		});
		comboBox.setBackground(new Color(255, 255, 255));
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"TAK", "NIE"}));
		panel.add(comboBox);
		
		JLabel lblPdzel = new JLabel("Sąsiedztwo:");
		panel.add(lblPdzel);
		
		final JComboBox comboBox_1 = new JComboBox();
		comboBox_1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent argP) {
				Core.Config.sasiedztwo = comboBox_1.getSelectedIndex();
				//System.out.println("Wybrano pedzel: "+Core.Config.pedzel+" ");
			}
		});
		comboBox_1.setBackground(new Color(255, 255, 255));
		comboBox_1.setModel(new DefaultComboBoxModel(this.pedzle));
		panel.add(comboBox_1);
		
		// =================================	generuj grafike 	================================

		panel_1 = new Plansza();
		panel_1.setBackground(SystemColor.text);
		contentPane.add(panel_1, BorderLayout.CENTER);

	    new Thread(panel_1).start();  

		// =================================	akcja generowania 	================================
	    this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            	panel_1.refresh();
            }
        });
	    
		JButton btnGeneruj = new JButton("START");
		btnGeneruj.setBackground(Color.GREEN);
		btnGeneruj.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Core.Config.menuClean) Display.this.panel_1.clean();
				if (Core.Config.menuSaveMap) {
					if (!Core.pkts.isEmpty()) {
						Display.this.panel_1.clean();
						Display.this.panel_1.loadMap();
					} else {
						Display.this.panel_1.random();
						Display.this.panel_1.saveMap();
					}
				} else {
					Display.this.panel_1.random();
				}
				Display.this.panel_1.refresh();
				Core.Config.StatusStart = 1;
			}
		});
		
		JLabel lblIloZiaren = new JLabel("Ziarna:");
		panel.add(lblIloZiaren);
		
		final JSpinner spinner = new JSpinner();
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Core.Config.punkty = (Integer) spinner.getValue();
			}
		});
		spinner.setModel(new SpinnerNumberModel(Core.Config.punkty, 1, 360, 1));
		panel.add(spinner);
		
		JLabel label = new JLabel("    ");
		panel.add(label);
		panel.add(btnGeneruj);
		
		JButton btnKoniec = new JButton("STOP");
		btnKoniec.setBackground(Color.RED);
		btnKoniec.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Core.Config.StatusStart = 0;
			}
		});
		panel.add(btnKoniec);
		
		JButton btnReset = new JButton("RESET");
		btnReset.setBackground(Color.BLACK);
		btnReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Display.this.panel_1.clean();
				Display.this.panel_1.refresh();
			}
		});
		
		JButton btnNowyPkt = new JButton("NOWY PKT");
		btnNowyPkt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Display.this.panel_1.randomPixel();
			}
		});
		btnNowyPkt.setBackground(Color.CYAN);
		panel.add(btnNowyPkt);
		panel.add(btnReset);
	}

}
