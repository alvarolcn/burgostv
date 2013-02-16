package com.alvarolara.burgostv;

import java.util.List;

import com.alvarolara.burgostv.utiles.CargadorImagenes;
import com.alvarolara.burgostv.utiles.ParseadorXML;
import com.alvarolara.burgostv.utiles.JustificaTexto;
import com.alvarolara.burgostv.utiles.Utilidades;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

/**
 * Visualiza un objeto y su descripcion.
 * @author Alvaro Lara Cano
 *
 */
public class ObjetoActivity extends Activity {

	/**
	 * Titulo del objeto.
	 */
	String titulo = "";

	/**
	 * Url_foto del objeto.
	 */
	String url_foto = "";

	/**
	 * Descripcion del objeto.
	 */
	String descripcion = "";

	/**
	 * Url_video del objeto.
	 */
	String url_video = "";

	
	/**
	 * Llamado cuando la Actividad es creada.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.objeto);

		// Obtener datos del intent.
		Intent in = getIntent();
		// Saber la procedencia del intent, recogiendo datos como el titulo.
		//Si no procede del intent, recogemos parametros para descargar de internet.
		if (in.getSerializableExtra(Utilidades.KEY_TITULO) == null) {

			String url = "http://www.burgostv.es/android/xml-url.php?tipo=";

			// Procede de una URL
			Uri datos = in.getData();

			List<String> params = datos.getPathSegments();

			// Saber que parametro es el primero.
			if (params.get(0).compareTo("noticias") == 0) {
				// Si son noticias.

				url += "noticia&url=" + params.get(4);

				cargaXml(url);

			} else if (params.get(0).compareTo("reportajes") == 0) {
				// Si son noticias.

				url += "reportaje&url=" + params.get(2);

				cargaXml(url);

			} else if (params.get(0).compareTo("mas-deporte") == 0) {
				// Si son noticias.

				url += "mas_deporte&url=" + params.get(4);

				cargaXml(url);

			} else if (params.get(0).compareTo("burgos-en-persona") == 0) {
				// Si son noticias.

				url += "burgos_en_persona&url=" + params.get(4);

				cargaXml(url);

			} else {
				// Mostrar mensaje de error
				Toast.makeText(getApplicationContext(),
						"La url no se puede abrir.", Toast.LENGTH_LONG).show();

				// Salir de la aplicacion.
				finish();
			}
		} else {

			// Obtener los datos del intent anterior.
			titulo = in.getStringExtra(Utilidades.KEY_TITULO);
			url_foto = in.getStringExtra(Utilidades.KEY_URL_FOTO);

			descripcion = in.getStringExtra(Utilidades.KEY_DESCRIPCION);
			url_video = in.getStringExtra(Utilidades.KEY_URL_VIDEO);

		}

		//Establecer el titulo.
		TextView TVtitulo = (TextView) findViewById(R.id.TVtitulo);
		TVtitulo.setText(titulo);
		
		//Establecer descripcion.
		TextView TVdescripcion = (TextView) findViewById(R.id.TVdescripcion);
		TVdescripcion.setText(descripcion);
		// Justificar la descripcion (como se vea, depende de cada pantalla).
		JustificaTexto.justifica(((TextView) findViewById(R.id.TVdescripcion)),	340f);
		
		
		//Añadimos la foto de la noticia.
		ImageView IVurl_foto = (ImageView) findViewById(R.id.IVurl_foto);
		
		// Cargar la nueva imagen a partir de la URL.
		CargadorImagenes cargadorImagenes = new CargadorImagenes(getApplicationContext());
		//Cargarla en tamano grande G.
		cargadorImagenes.muestraImagen(url_foto, IVurl_foto, "G");
					
		//Si la imagen se ha cargado correctamente, añadimos el listener.
		if(!in.getStringExtra("cargando").equals("si")){
			System.out.println("IMAGEN CORRECTAAAAAAAA");
			
			//Alpha a la imagen
			IVurl_foto.setAlpha(200);
			
			//Click listener.
			IVurl_foto.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					// Comprobar la conexion a internet.
					if (Utilidades.hayInternet(ObjetoActivity.this, false)) {
						
						// Intent para videoactivity.
						Intent in = new Intent(getApplicationContext(),
								VideoActivity.class);
						in.putExtra(Utilidades.KEY_URL_VIDEO, url_video);
						startActivity(in);
					}
				}
			});
		}else{
			//No añadimos el listener y ocultamos el boton de reproducir.
			System.out.println("IMAGEN NOOO CORRECTAAAAAAAA");
			
			//Ocultar el play y no añadir el listener.
			((ImageView) findViewById(R.id.IVplay)).setVisibility(View.GONE);
		}
			
	

			
				
			
		

	}

	public void cargaXml(String url) {

		ParseadorXML parser = new ParseadorXML(url, Utilidades.KEY_ITEM_OBJETO);

		try {
			
			// Si la longitud del nodelist es 0 al buscar la clave 'item',
			// lanzamos excepcion.
			if (parser.getNodeList().getLength() == 0) {
				throw new Exception();
			} else {
				// Añadir cada nodo hijo al hashmap con clave=> valor.
				titulo = parser.getValor(Utilidades.KEY_TITULO);
				descripcion = parser.getValor(Utilidades.KEY_DESCRIPCION);
				url_foto = parser.getValor(Utilidades.KEY_URL_FOTO);
				url_video = parser.getValor(Utilidades.KEY_URL_VIDEO);
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Error al abrir la URL.",
					Toast.LENGTH_LONG).show();
		}
	}

}
