package igu;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;	
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//Neo4J
import org.neo4j.driver.v1.*;

import persistence.Persistence;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;

@SuppressWarnings("rawtypes")
public class VentanaPrincipal extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;
	private JTextField txtEfectividad;
	private JButton btnObt;
	private Driver driver;
	private JButton btnCalcular;
	private JComboBox cbAtaque;
	private JComboBox cbPoke1;
	private JComboBox cbPoke2;
	private JTextArea txtArea1;
	private JComboBox cbPokeDanox2;
	private JLabel lblHaceUnDao;
	private JComboBox cbDano;
	private JLabel lblEnUnPokemon;
	private JLabel lblCalcularEfectividadDe;
	private JTextArea txaTipoMasFuerte;
	private JLabel lblUsuario;
	private JLabel lblContrasea;
	protected static final int WARNING_MESSAGE = 2;
	private JLabel lblElSiguienteTipo;
	private JLabel lblElSiguienteTipo_1;
	private JTextField textMejor;
	private JButton btnCalcularTiopMas;
	private JButton btnCalcularMejorTipo;
	private JLabel lblLosSiguientesTipos;
	private JButton btnCalcularMejorCombinacion;
	private JTextField textMejorDefensa;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal frame = new VentanaPrincipal();
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
	public VentanaPrincipal() {

		JPanel panel = new JPanel();
		JLabel label = new JLabel("Introduzca su usuario y contraseña de Neo4J (por defecto neo4j / admin):"); 
		JTextField user=new JTextField(20);
		JPasswordField pass = new JPasswordField(20); 
		panel.add(label); 
		panel.add(user); 
		panel.add(pass); 
		String[] options = new String[]{"Exit","OK"}; 
		int option = JOptionPane.showOptionDialog(null, panel, "Autenticacion", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]); 
		panel.add(getLblUsuario());
		panel.add(getLblContrasea());
		if(option == 1) { // pressing OK button 

			String password = String.valueOf(pass.getPassword()); 
			String usuario=user.getText();

			driver=Persistence.conectar(password, usuario, driver, WARNING_MESSAGE);			

			setTitle("Aplicacion de prueba");
			setResizable(false);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 730, 629);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblObtenerLosTipos = new JLabel("Obtener los tipos a los que el tipo");
			lblObtenerLosTipos.setBounds(10, 11, 318, 14);
			contentPane.add(lblObtenerLosTipos);
			contentPane.add(getTextArea());

			JLabel lblTipoDelAtaque = new JLabel("Tipo del ataque:");
			lblTipoDelAtaque.setBounds(10, 207, 106, 14);
			contentPane.add(lblTipoDelAtaque);

			JLabel lblTiposDelPokemon = new JLabel("Tipos del pokemon:");
			lblTiposDelPokemon.setBounds(10, 238, 116, 14);
			contentPane.add(lblTiposDelPokemon);

			txtEfectividad = new JTextField();
			txtEfectividad.setEditable(false);
			txtEfectividad.setText("Efectividad total: ");
			txtEfectividad.setBounds(126, 264, 441, 20);
			contentPane.add(txtEfectividad);
			txtEfectividad.setColumns(10);
			contentPane.add(getBtnObt());
			contentPane.add(getBtnCalcular());
			contentPane.add(getCbAtaque());
			contentPane.add(getCbPoke1());
			contentPane.add(getCbPoke2());
			contentPane.add(getTxtArea1());
			contentPane.add(getCbPokeDanox2());
			contentPane.add(getLblHaceUnDao());
			contentPane.add(getCbDano());
			contentPane.add(getLblEnUnPokemon());
			contentPane.add(getLblCalcularEfectividadDe());
			contentPane.add(getTxaTipoMasFuerte());
			contentPane.add(getLblElSiguienteTipo());
			contentPane.add(getLblElSiguienteTipo_1());
			contentPane.add(getTextMejor());
			contentPane.add(getBtnCalcularTiopMas());
			contentPane.add(getBtnCalcularMejorTipo());
			contentPane.add(getLblLosSiguientesTipos());
			contentPane.add(getBtnCalcularMejorCombinacion());
			contentPane.add(getTextMejorDefensa());
			this.setLocationRelativeTo(null);

		}
		else {
			System.exit(0);
		}
	}

	public JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setBounds(10, 86, 557, 70);
		}
		return textArea;
	}
	public JButton getBtnObt() {
		if (btnObt == null) {
			btnObt = new JButton("Obtener");
			btnObt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String resultado = Persistence.obtenerTiposALosQueUnPokemonHaceUnDanoMultiplicadoPor(driver,textArea,cbDano,cbPokeDanox2);
					textArea.setText(resultado);
				}
			});
			btnObt.setBounds(10, 52, 89, 23);
		}
		return btnObt;
	}
	private JButton getBtnCalcular() {
		if (btnCalcular == null) {
			btnCalcular = new JButton("Calcular");
			btnCalcular.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Persistence.calcularEfectividadDeUnAtaqueEnUnPokemonDeTipos(driver, txtArea1, cbAtaque, cbPoke1, cbPoke2, txtEfectividad);
				}
			});
			btnCalcular.setBounds(10, 263, 89, 23);
		}
		return btnCalcular;
	}
	@SuppressWarnings("unchecked")
	private JComboBox getCbAtaque() {
		if (cbAtaque == null) {
			cbAtaque = new JComboBox();
			cbAtaque.setModel(new DefaultComboBoxModel(Persistence.getTipos(driver, false)));
			cbAtaque.setBounds(126, 204, 139, 20);
		}
		return cbAtaque;
	}
	@SuppressWarnings("unchecked")
	private JComboBox getCbPoke1() {
		if (cbPoke1 == null) {
			cbPoke1 = new JComboBox();
			cbPoke1.setModel(new DefaultComboBoxModel(Persistence.getTipos(driver, false)));
			cbPoke1.setBounds(126, 235, 139, 20);
		}
		return cbPoke1;
	}
	@SuppressWarnings("unchecked")
	private JComboBox getCbPoke2() {
		if (cbPoke2 == null) {
			cbPoke2 = new JComboBox();
			cbPoke2.setModel(new DefaultComboBoxModel(Persistence.getTipos(driver, true)));
			cbPoke2.setBounds(275, 235, 139, 20);
		}
		return cbPoke2;
	}
	private JTextArea getTxtArea1() {
		if (txtArea1 == null) {
			txtArea1 = new JTextArea();
			txtArea1.setBounds(10, 297, 557, 70);
		}
		return txtArea1;
	}
	@SuppressWarnings("unchecked")
	private JComboBox getCbPokeDanox2() {
		if (cbPokeDanox2 == null) {
			cbPokeDanox2 = new JComboBox();
			cbPokeDanox2.setModel(new DefaultComboBoxModel(Persistence.getTipos(driver, false)));
			cbPokeDanox2.setBounds(215, 8, 139, 20);
		}
		return cbPokeDanox2;
	}
	private JLabel getLblHaceUnDao() {
		if (lblHaceUnDao == null) {
			lblHaceUnDao = new JLabel("hace un da\u00F1o multiplicado por:");
			lblHaceUnDao.setBounds(10, 36, 212, 14);
		}
		return lblHaceUnDao;
	}
	@SuppressWarnings("unchecked")
	private JComboBox getCbDano() {
		if (cbDano == null) {
			cbDano = new JComboBox();
			cbDano.setModel(new DefaultComboBoxModel(new String[] {"0", "0.5", "2"}));
			cbDano.setBounds(215, 36, 67, 20);
		}
		return cbDano;
	}
	private JLabel getLblEnUnPokemon() {
		if (lblEnUnPokemon == null) {
			lblEnUnPokemon = new JLabel("en un pokemon de un tipo o tipos determinados:");
			lblEnUnPokemon.setBounds(10, 181, 442, 14);
		}
		return lblEnUnPokemon;
	}
	private JLabel getLblCalcularEfectividadDe() {
		if (lblCalcularEfectividadDe == null) {
			lblCalcularEfectividadDe = new JLabel("Calcular efectividad de un ataque de un determinado tipo ");
			lblCalcularEfectividadDe.setBounds(10, 167, 442, 14);
		}
		return lblCalcularEfectividadDe;
	}
	private JTextArea getTxaTipoMasFuerte() {
		if (txaTipoMasFuerte == null) {
			txaTipoMasFuerte = new JTextArea();
			txaTipoMasFuerte.setEditable(false);
			txaTipoMasFuerte.setBounds(287, 423, 139, 23);
		}
		return txaTipoMasFuerte;
	}

	private JLabel getLblUsuario() {
		if (lblUsuario == null) {
			lblUsuario = new JLabel("Usuario:");
			lblUsuario.setBounds(39, 60, 166, 14);
		}
		return lblUsuario;
	}
	private JLabel getLblContrasea() {
		if (lblContrasea == null) {
			lblContrasea = new JLabel("Contrase\u00F1a:");
			lblContrasea.setBounds(39, 116, 166, 14);
		}
		return lblContrasea;
	}
	private JLabel getLblElSiguienteTipo() {
		if (lblElSiguienteTipo == null) {
			lblElSiguienteTipo = new JLabel("El siguiente tipo es el que mas da\u00F1o hace de media:");
			lblElSiguienteTipo.setBounds(10, 400, 557, 14);
		}
		return lblElSiguienteTipo;
	}
	private JLabel getLblElSiguienteTipo_1() {
		if (lblElSiguienteTipo_1 == null) {
			lblElSiguienteTipo_1 = new JLabel("El siguiente tipo es el mejor teniendo en cuenta el n\u00BA de tipos a los que vence / el n\u00BA de tipos por los que es vencido");
			lblElSiguienteTipo_1.setBounds(10, 457, 839, 14);
		}
		return lblElSiguienteTipo_1;
	}
	private JTextField getTextMejor() {
		if (textMejor == null) {
			textMejor = new JTextField();
			textMejor.setEditable(false);
			textMejor.setBounds(287, 482, 142, 20);
			textMejor.setColumns(10);
		}
		return textMejor;
	}
	private JButton getBtnCalcularTiopMas() {
		if (btnCalcularTiopMas == null) {
			btnCalcularTiopMas = new JButton("Calcular tipo mas fuerte");
			btnCalcularTiopMas.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					txaTipoMasFuerte.setText(Persistence.calcularTipoMasFuerte(driver));
				}
			});
			btnCalcularTiopMas.setBounds(10, 421, 226, 23);
		}
		return btnCalcularTiopMas;
	}
	private JButton getBtnCalcularMejorTipo() {
		if (btnCalcularMejorTipo == null) {
			btnCalcularMejorTipo = new JButton("Calcular mejor tipo");
			btnCalcularMejorTipo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					textMejor.setText(Persistence.CalcularMejorTipo(driver));
				}
			});
			btnCalcularMejorTipo.setBounds(10, 482, 226, 23);
		}
		return btnCalcularMejorTipo;
	}
	private JLabel getLblLosSiguientesTipos() {
		if (lblLosSiguientesTipos == null) {
			lblLosSiguientesTipos = new JLabel("Los siguientes tipos son los optimos para una mejor defensa:");
			lblLosSiguientesTipos.setBounds(10, 516, 537, 14);
		}
		return lblLosSiguientesTipos;
	}
	private JButton getBtnCalcularMejorCombinacion() {
		if (btnCalcularMejorCombinacion == null) {
			btnCalcularMejorCombinacion = new JButton("Calcular mejor combinacion de tipos para mejor defensa");
			btnCalcularMejorCombinacion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					textMejorDefensa.setText(Persistence.calcularMejorCombinacionDeTipos(driver));
				}
			});
			btnCalcularMejorCombinacion.setBounds(10, 541, 429, 23);
		}
		return btnCalcularMejorCombinacion;
	}
	private JTextField getTextMejorDefensa() {
		if (textMejorDefensa == null) {
			textMejorDefensa = new JTextField();
			textMejorDefensa.setEditable(false);
			textMejorDefensa.setColumns(10);
			textMejorDefensa.setBounds(450, 542, 220, 20);
		}
		return textMejorDefensa;
	}
}
