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

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * GUI
 */
public class Display extends JFrame {

	private static final long serialVersionUID = 1L;
	private Plansza panel_1 = null;
	private JPanel contentPane;
	private String[] pedzle = new String[] {"Moore", "von Nauman", "Hexagonal left", "Hexagonal right", "Hexagonal random", "Pentagonal left", "Pentagonal right", "Pentagonal top", "Pentagonal bottom", "Pentagonal random"};
	private JCheckBoxMenuItem chckbxmntmPamitajRozmieszczenieZiaren;
	private JCheckBoxMenuItem chckbxmntmCzyPlanszePrzed;
	private JRadioButtonMenuItem rdbtnmntmRwnomierne;
	private JRadioButtonMenuItem rdbtnmntmLosowepromieKoa;
	private JRadioButtonMenuItem rdbtnmntmLosowePrzypadkowe;
	private ButtonGroup roz_zarodkow;
	private ButtonGroup bg_periodycznosc;
	private JSpinner spinner_1;
	
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
		
		JMenu mnRozmieszczenieZarodkw = new JMenu("Rozmieszczenie zarodków");
		menuBar.add(mnRozmieszczenieZarodkw);
		
		rdbtnmntmRwnomierne = new JRadioButtonMenuItem("Równomierne");
		rdbtnmntmRwnomierne.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Core.Config.rozmieszczenie = 1;
			}
		});
		mnRozmieszczenieZarodkw.add(rdbtnmntmRwnomierne);
		
		rdbtnmntmLosowepromieKoa = new JRadioButtonMenuItem("Losowe (promień koła)");
		rdbtnmntmLosowepromieKoa.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Core.Config.rozmieszczenie = 2;
			}
		});
		rdbtnmntmLosowepromieKoa.setSelected(true);
		mnRozmieszczenieZarodkw.add(rdbtnmntmLosowepromieKoa);
		
		rdbtnmntmLosowePrzypadkowe = new JRadioButtonMenuItem("Losowe przypadkowe");
		rdbtnmntmLosowePrzypadkowe.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Core.Config.rozmieszczenie = 3;
			}
		});
		mnRozmieszczenieZarodkw.add(rdbtnmntmLosowePrzypadkowe);
		
		
		
		JRadioButtonMenuItem rdbtnmntmLosoweZPromieniem = new JRadioButtonMenuItem("Losowe z promieniem");
		rdbtnmntmLosoweZPromieniem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Core.Config.rozmieszczenie = 4;
			}
		});
		mnRozmieszczenieZarodkw.add(rdbtnmntmLosoweZPromieniem);

		roz_zarodkow = new ButtonGroup();
		roz_zarodkow.add(rdbtnmntmRwnomierne);
		roz_zarodkow.add(rdbtnmntmLosowepromieKoa);
		roz_zarodkow.add(rdbtnmntmLosowePrzypadkowe);
		roz_zarodkow.add(rdbtnmntmLosoweZPromieniem);
		
		//===========================
		JMenu mnPeriodyczno = new JMenu("Periodyczność");
		menuBar.add(mnPeriodyczno);
		
		JRadioButtonMenuItem rdbtnmntmTak = new JRadioButtonMenuItem("Tak");
		rdbtnmntmTak.setSelected(true);
		rdbtnmntmTak.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Core.Config.bc = 0;
			}
		});
		mnPeriodyczno.add(rdbtnmntmTak);
		
		JRadioButtonMenuItem rdbtnmntmNie = new JRadioButtonMenuItem("Nie");
		rdbtnmntmNie.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Core.Config.bc=1;
			}
		});
		mnPeriodyczno.add(rdbtnmntmNie);


		bg_periodycznosc = new ButtonGroup();
		bg_periodycznosc.add(rdbtnmntmTak);
		bg_periodycznosc.add(rdbtnmntmNie);
		
		/*JMenu mnMetodaRozrostu = new JMenu("Metoda rozrostu ziaren");
		menuBar.add(mnMetodaRozrostu);
		
		JRadioButtonMenuItem rdbtnmntmNaiwnyRozrostZiaren = new JRadioButtonMenuItem("Naiwny rozrost ziaren");
		rdbtnmntmNaiwnyRozrostZiaren.setSelected(true);
		rdbtnmntmNaiwnyRozrostZiaren.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				Core.Config.metoda = 0;
			}
		});
		mnMetodaRozrostu.add(rdbtnmntmNaiwnyRozrostZiaren);
		
		JRadioButtonMenuItem rdbtnmntmZarodkowanie = new JRadioButtonMenuItem("Zarodkowanie");
		rdbtnmntmZarodkowanie.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Core.Config.metoda = 1;
			}
		});
		rdbtnmntmZarodkowanie.setSelected(true);
		mnMetodaRozrostu.add(rdbtnmntmZarodkowanie);
		
		ButtonGroup bg_metoda = new ButtonGroup();
		bg_metoda.add(rdbtnmntmNaiwnyRozrostZiaren);
		bg_metoda.add(rdbtnmntmZarodkowanie);
*/
		//ButtonGroup rek_metoda = new ButtonGroup();
		
		//================================================
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
		
		JLabel label_1 = new JLabel(" ");
		panel.add(label_1);
		
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
		
		JLabel label = new JLabel(" ");
		panel.add(label);
		
		JLabel lblCzas = new JLabel("Czas");
		panel.add(lblCzas);
		
		spinner_1 = new JSpinner();
		spinner_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Core.Config.delay = (Integer) spinner_1.getValue();
			}
		});
		spinner_1.setModel(new SpinnerNumberModel(20, 1, 9999, 1));
		panel.add(spinner_1);
		
		JLabel lblMs = new JLabel("ms");
		panel.add(lblMs);
		
		JLabel label_2 = new JLabel(" ");
		panel.add(label_2);
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
		panel.add(btnReset);
		
		JButton btnRekrystalizacja = new JButton("REKRYSTALIZACJA");
		btnRekrystalizacja.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Core.Config.rekrystalizacja = 1;
			}
		});
		panel.add(btnRekrystalizacja);
	}

}
