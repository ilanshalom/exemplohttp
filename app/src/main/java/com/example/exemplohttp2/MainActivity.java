package com.example.exemplohttp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView txtResp = null;
    private EditText txtpais = null;
    private ProgressDialog progressDialog ;
    private ImageView figmundo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            ImageView figmundo = (ImageView) findViewById(R.id.idfig);
            figmundo.setVisibility(View.INVISIBLE);
        } catch (Exception ee) {
            Toast.makeText(getApplicationContext(), ee.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void lerPais(View view) {
        figmundo = (ImageView) findViewById(R.id.idfig);
        figmundo.setVisibility(View.INVISIBLE);
        if (checkInternetConection()){
            progressDialog = ProgressDialog.show(this, "", "Obtendo dados");
            txtpais = (EditText)findViewById(R.id.txtpais);
            final String paisSolicitado = txtpais.getText().toString();
            new DownloadDadosPais().execute("http://mfpledon.com.br/paisesTxt.php?pais="+ paisSolicitado);
        } else{
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Sem conexão. Verifique.",Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkInternetConection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void mostraResposta(String resposta){
        figmundo = (ImageView) findViewById(R.id.idfig);
        txtResp = (TextView) findViewById(R.id.txthttp);
        if(resposta.equalsIgnoreCase("País não cadastrado."))figmundo.setVisibility(View.INVISIBLE);
             else figmundo.setVisibility(View.VISIBLE);
        txtResp.setText("\n\n" + resposta);
    }

     private class DownloadDadosPais extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // params[0] é o URL
            try {
                return downloadDados(params[0]);  // params[0] é o URL
            } catch (IOException e) {
                progressDialog.dismiss();
                return "URL inválido";
            }
        }

        // onPostExecute exibe o resultado do AsyncTask
        @Override
        protected void onPostExecute(String result) {
           progressDialog.dismiss();
           mostraResposta(result);
        }

        private String downloadDados(String myurl) throws IOException {
            InputStream is = null;
            String respostaHttp = "";
            HttpURLConnection conn = null;
            InputStream in = null;
            ByteArrayOutputStream bos = null;
            try {
                URL u = new URL(myurl);
                conn = (HttpURLConnection) u.openConnection();
                conn.setConnectTimeout(9000); // 9 segundos de timeout
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                in = conn.getInputStream();
                bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
                }
                respostaHttp = bos.toString("UTF-8");
                return respostaHttp;
            } finally {
                if (in != null) in.close();
            }
        }

    }
}
