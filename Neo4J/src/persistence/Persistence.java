package persistence;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//Neo4J
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.ClientException;

@SuppressWarnings("rawtypes")
public class Persistence {

	public static Driver conectar(String password, String usuario, Driver driver, int WARNING_MESSAGE) {
		// Neo4J establecer conexión
		Config noSSL = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();
		driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(usuario, password), noSSL);

		// Comprobar que el usuario y contra son correctos, para ello hay que hacer una
		// pequeña consulta
		try (Session session = driver.session()) {
			StatementResult result;
			String foafQuery = "MATCH (x) RETURN x";
			result = session.run(foafQuery);
			result.hasNext();// Si no da excepcion, es correcto el usuario
		} catch (ClientException e) {
			JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.", "Lo sentimos", WARNING_MESSAGE);
			System.exit(0);
		}
		return driver;
	}

	public static String calcularTipoMasFuerte(Driver driver) {
		String tipo = "???";
		String[] tipos = getTipos(driver, false);
		// Porque puede no tener tipo secundario
		String[] tipos2 = getTipos(driver, true);
		try (Session session = driver.session()) {
			double danoTotal = 0;
			for (String ataque : tipos) {
				// para cada tipo de ataque
				int x2 = 0;
				int x05 = 0;
				int x0 = 0;
				int x1 = 0;
				for (String tipo1 : tipos) {
					// para cada tipo primario
					// Pongo a 0 las posibles debilidades y fortalezas
					for (String tipo2 : tipos2) {
						// para cada tipo secundario
						StatementResult result;
						// Hallar proporcion para el tipo 1 del pokemon
						String Query = "";
						// Si los tipos son distintos (absurdo)
						if (tipo1 != tipo2) {
							// Si el tipo 2 no esta vacio (solo tiene uno u ambos)
							if (!tipo2.isEmpty())
								Query = "match(t:tipo{nombreTipo:'" + ataque + "'})-[h:HaceDano]->(:tipo{nombreTipo:'"
										+ tipo1 + "'}),(t)-[h2:HaceDano]->(:tipo{nombreTipo:'" + tipo2
										+ "'}) return h.proporcion,h2.proporcion";
							else
								Query = "match(t:tipo{nombreTipo:'" + ataque + "'})-[h:HaceDano]->(:tipo{nombreTipo:'"
										+ tipo1 + "'}) return h.proporcion";
							// Coge el resultado
							result = session.run(Query);
							// Si hay resultado es que hay una relacion x0,x05,x2
							if (result.hasNext()) {
								// Si es 1, solo hay un posible valor
								if (result.keys().size() == 1) {
									double proporcion = Double.valueOf(result.list().get(0).values().get(0).toString());
									if (proporcion >= 0 && proporcion < 1)
										x05++;
									else if (proporcion == 0)
										x0++;
									else if (proporcion == 1)
										x1++;
									else if (proporcion >= 2)
										x2++;
								}
								// Si hay 2, tiene el valor del tipo primario y secundario
								else if (result.keys().size() == 2) {
									List<Value> values = result.list().get(0).values();
									double proporcion = Double.valueOf(values.get(0).toString())
											* Double.valueOf(values.get(1).toString());
									if (proporcion >= 0 && proporcion < 1)
										x05++;
									else if (proporcion == 0)
										x0++;
									else if (proporcion == 1)
										x1++;
									else if (proporcion >= 2)
										x2++;
								}
							}
							// Si no hay relacion es x1.
							else
								x1++;
						}
					}
				}
				// Una idea, se suma todo multiplicando y temporalmente el que mas sume, es el
				// mejor
				double danoTmp = x2 * 2 + x1 * 1 + x0 * 0 + x05 * 0.5;
				if (danoTmp > danoTotal) {
					danoTotal = danoTmp;
					// Para depurar o verlo por pantalla.
					// System.out.println("tipo: "+ataque+", ataque: "+danoTotal);
					tipo = ataque;
				}
			}
		}
		return tipo;
	}

	public static String obtenerTiposALosQueUnPokemonHaceUnDanoMultiplicadoPor(Driver driver, JTextArea textArea,
			JComboBox cbDano, JComboBox cbPokeDanox2) {
		String resultado = "";
		try (Session session = driver.session()) {
			textArea.setText("");
			StatementResult result;
			String foafQuery = "MATCH (x)-[:HaceDano{proporcion:" + cbDano.getSelectedItem().toString()
					+ "}]->(y) WHERE x.nombreTipo= {tipo} RETURN y.nombreTipo";
			result = session.run(foafQuery, parameters("tipo", cbPokeDanox2.getSelectedItem().toString()));
			while (result.hasNext())
				resultado += " " + result.next().get(0);
		}
		return resultado;
	}

	public static void calcularEfectividadDeUnAtaqueEnUnPokemonDeTipos(Driver driver, JTextArea txtArea1,
			JComboBox cbAtaque, JComboBox cbPoke1, JComboBox cbPoke2, JTextField txtEfectividad) {
		try (Session session = driver.session()) {
			txtArea1.setText("");

			Double ataqueATipo1 = null;

			// Hallar proporcion para el tipo 1 del pokemon
			StatementResult result;
			String foafQuery = "match (x)-[:HaceDano{proporcion:0}]->(y) where x.nombreTipo=\""
					+ cbAtaque.getSelectedItem().toString() + "\" and y.nombreTipo=\""
					+ cbPoke1.getSelectedItem().toString() + "\" return x,y";
			result = session.run(foafQuery);
			if (result.hasNext())
				ataqueATipo1 = 0.0;
			else {
				foafQuery = "match (x)-[:HaceDano{proporcion:0.5}]->(y) where x.nombreTipo=\""
						+ cbAtaque.getSelectedItem().toString() + "\" and y.nombreTipo=\""
						+ cbPoke1.getSelectedItem().toString() + "\" return x,y";
				result = session.run(foafQuery);
				if (result.hasNext())
					ataqueATipo1 = 0.5;
				else {
					foafQuery = "match (x)-[:HaceDano{proporcion:2}]->(y) where x.nombreTipo=\""
							+ cbAtaque.getSelectedItem().toString() + "\" and y.nombreTipo=\""
							+ cbPoke1.getSelectedItem().toString() + "\" return x,y";
					result = session.run(foafQuery);
					if (result.hasNext())
						ataqueATipo1 = 2.0;
					else {
						ataqueATipo1 = 1.0;
					}
				}
			}
			txtArea1.setText("Efectividad de una ataque de tipo " + cbAtaque.getSelectedItem().toString()
					+ " sobre un pokemon tipo: " + cbPoke1.getSelectedItem().toString() + " -> " + ataqueATipo1 + "\n");
			if (!cbPoke2.getSelectedItem().toString().isEmpty()) {
				StatementResult result2;
				Double ataqueATipo2 = null;
				// Hallar proporcion para el tipo 1 del pokemon
				foafQuery =

						"match (x)-[:HaceDano{proporcion:0}]->(y) where x.nombreTipo=\""
								+ cbAtaque.getSelectedItem().toString() + "\" and y.nombreTipo=\""
								+ cbPoke2.getSelectedItem().toString() + "\" return x,y";
				result2 = session.run(foafQuery);
				if (result2.hasNext())
					ataqueATipo2 = 0.0;
				else {
					foafQuery = "match (x)-[:HaceDano{proporcion:0.5}]->(y) where x.nombreTipo=\""
							+ cbAtaque.getSelectedItem().toString() + "\" and y.nombreTipo=\""
							+ cbPoke2.getSelectedItem().toString() + "\" return x,y";
					result2 = session.run(foafQuery);
					if (result2.hasNext())
						ataqueATipo2 = 0.5;
					else {
						foafQuery = "match (x)-[:HaceDano{proporcion:2}]->(y) where x.nombreTipo=\""
								+ cbAtaque.getSelectedItem().toString() + "\" and y.nombreTipo=\""
								+ cbPoke2.getSelectedItem().toString() + "\" return x,y";
						result2 = session.run(foafQuery);
						if (result2.hasNext())
							ataqueATipo2 = 2.0;
						else {
							ataqueATipo2 = 1.0;
						}
					}
				}
				txtArea1.setText(txtArea1.getText() + "Efectividad de una ataque de tipo "
						+ cbAtaque.getSelectedItem().toString() + " sobre un pokemon tipo "
						+ cbPoke2.getSelectedItem().toString() + " -> " + ataqueATipo2);

				txtEfectividad.setText("Efectividad total: ");

				// Para que no se produzca el efecto: ataque de agua a pokemon tipo fuego fuego = x4
				// Ya que no hay pokemon que tengan dos tipos iguales
				String tipo1=cbPoke1.getSelectedItem().toString();
				String tipo2=cbPoke2.getSelectedItem().toString();
				if(tipo1.equals(tipo2))
					txtEfectividad.setText(txtEfectividad.getText() + " " + ataqueATipo1+" (no hay pokemon de dos tipos iguales)");
				else
					txtEfectividad.setText(txtEfectividad.getText() + " " + ataqueATipo1 * ataqueATipo2);

			} else {
				txtEfectividad.setText("Efectividad total: ");
				txtEfectividad.setText(txtEfectividad.getText() + " " + ataqueATipo1);
			}
		}
	}
	public static double calcularEfectividadDeUnAtaqueEnUnPokemonDeTipos(Driver driver,
			String tipoAtaque, String tipo1DelPokemon, String tipo2DelPokemon) {
		try (Session session = driver.session()) {

			Double ataqueATipo1 = null;

			// Hallar proporcion para el tipo 1 del pokemon
			StatementResult result;
			String foafQuery = "match (x)-[:HaceDano{proporcion:0}]->(y) where x.nombreTipo=\""
					+ tipoAtaque + "\" and y.nombreTipo=\""
					+ tipo1DelPokemon + "\" return x,y";
			result = session.run(foafQuery);
			if (result.hasNext())
				ataqueATipo1 = 0.0;
			else {
				foafQuery = "match (x)-[:HaceDano{proporcion:0.5}]->(y) where x.nombreTipo=\""
						+ tipoAtaque + "\" and y.nombreTipo=\""
						+ tipo1DelPokemon + "\" return x,y";
				result = session.run(foafQuery);
				if (result.hasNext())
					ataqueATipo1 = 0.5;
				else {
					foafQuery = "match (x)-[:HaceDano{proporcion:2}]->(y) where x.nombreTipo=\""
							+ tipoAtaque + "\" and y.nombreTipo=\""
							+ tipo1DelPokemon + "\" return x,y";
					result = session.run(foafQuery);
					if (result.hasNext())
						ataqueATipo1 = 2.0;
					else {
						ataqueATipo1 = 1.0;
					}
				}
			}
			StatementResult result2;
			Double ataqueATipo2 = null;
			// Hallar proporcion para el tipo 1 del pokemon
			foafQuery =

					"match (x)-[:HaceDano{proporcion:0}]->(y) where x.nombreTipo=\""
							+ tipoAtaque + "\" and y.nombreTipo=\""
							+ tipo2DelPokemon + "\" return x,y";
			result2 = session.run(foafQuery);
			if (result2.hasNext())
				ataqueATipo2 = 0.0;
			else {
				foafQuery = "match (x)-[:HaceDano{proporcion:0.5}]->(y) where x.nombreTipo=\""
						+ tipoAtaque + "\" and y.nombreTipo=\""
						+ tipo2DelPokemon + "\" return x,y";
				result2 = session.run(foafQuery);
				if (result2.hasNext())
					ataqueATipo2 = 0.5;
				else {
					foafQuery = "match (x)-[:HaceDano{proporcion:2}]->(y) where x.nombreTipo=\""
							+ tipoAtaque + "\" and y.nombreTipo=\""
							+ tipo2DelPokemon + "\" return x,y";
					result2 = session.run(foafQuery);
					if (result2.hasNext())
						ataqueATipo2 = 2.0;
					else {
						ataqueATipo2 = 1.0;
					}
				}
			}

			double efectividadTotal;

			// Para que no se produzca el efecto: ataque de agua a pokemon tipo fuego fuego = x4
			// Ya que no hay pokemon que tengan dos tipos iguales
			if(tipo1DelPokemon.equals(tipo2DelPokemon))
				efectividadTotal=ataqueATipo1;
			else
				efectividadTotal=ataqueATipo1*ataqueATipo2;

			return efectividadTotal;
		}
	}

	public static String CalcularMejorTipo(Driver driver) {
		String[] tipos = getTipos(driver, false);
		double mejorResultado = 0;
		String tipoResultado = "";
		try (Session session = driver.session()) {
			for (String tipoActual : tipos) {
				double nDeX2Hechos = 0;
				double nDeX2Recibidos = 0;
				StatementResult result;
				String Query = "match (x {nombreTipo:\"" + tipoActual
						+ "\"}) -[r:HaceDano{proporcion:2}]->(y) return count(r)";
				result = session.run(Query);
				if (result.hasNext())
					nDeX2Hechos = Double.valueOf(result.list().get(0).values().get(0).toString());
				Query = "match (y) -[r:HaceDano{proporcion:2}]->(x {nombreTipo:\"" + tipoActual
						+ "\"}) return count(r)";
				result = session.run(Query);
				if (result.hasNext())
					nDeX2Recibidos = Double.valueOf(result.list().get(0).values().get(0).toString());

				if (nDeX2Recibidos != 0) {
					double resultadoActual = nDeX2Hechos / nDeX2Recibidos;
					if (resultadoActual > mejorResultado) {
						mejorResultado = resultadoActual;
						tipoResultado = tipoActual;
					}
				} else
					return tipoActual;
			}
		}
		return tipoResultado;
	}

	public static String[] getTipos(Driver driver, boolean conElementoVacio) {

		ArrayList<String> tiposArray = new ArrayList<String>();

		try (Session session = driver.session()) {
			StatementResult result;
			String Query = "MATCH (n:tipo) RETURN n.nombreTipo";
			result = session.run(Query);
			while (result.hasNext())
				tiposArray.add(result.next().get(0).toString().replace("\"", ""));
		}

		ArrayList<String> tiposConElementoVacioArray = new ArrayList<String>(tiposArray);
		tiposConElementoVacioArray.add(0, "");
		String[] tipos = new String[tiposArray.size()];
		tipos = tiposArray.toArray(tipos);
		String[] tiposConElementoVacio = new String[tiposConElementoVacioArray.size()];
		tiposConElementoVacio = tiposConElementoVacioArray.toArray(tiposConElementoVacio);

		// Devolver la lista dependiendo del boolean dado
		return conElementoVacio ? tiposConElementoVacio : tipos;

	}

	/**
	 * Calcula la mejor combinación de tipos en función de la defensa.
	 * Es decir, calcula la mejor combinación para una mejor defensa.
	 * @param driver
	 * @return
	 */
	public static String calcularMejorCombinacionDeTipos(Driver driver) {
		String[] tipos=getTipos(driver, false);
		String[] tipos2=tipos.clone();
		String[] tiposAtaque=tipos.clone();

		double mediaMejor=99999999;
		String mejorCombinacionDeTipos="";

		//Para cada combinación de tipos
		for(int i=0;i<tipos.length;i++) {//Tipo 1 del pokemon
			for(int j=0;j<tipos2.length;j++) {//Tipo 2 del pokemon
				//Hallamos la media de: efectividad de un ataque sobre un pokemon de esos tipos
				//Y eso lo hacemos para todos los posibles tipos de ataque
				double media=0.0;
				for(int k=0;k<tiposAtaque.length;k++)
					media+=calcularEfectividadDeUnAtaqueEnUnPokemonDeTipos(driver, tiposAtaque[k], tipos[i], tipos2[j]);
				media/=tipos.length;
				if(media<mediaMejor) {
					mediaMejor=media;
					mejorCombinacionDeTipos=tipos[i]+", "+tipos2[j];
				}
			}
		}
		return mejorCombinacionDeTipos;
	}

}
