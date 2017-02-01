package pe.edu.ulima.vistortexto.visortexto;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import spark.ModelAndView;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.stop;

public class Main {

    public static void main(String[] args) {
        get("/parar", (req, resp) -> {
            stop();
            return "";
        });

        get("/", (req, resp) -> {
            return new ModelAndView(null, "main.html");
        }, new Jinja2TemplateEngine());

        post("/mostrar", (req, resp) -> {
            String titulo = req.queryParams("titulo");
            String contenido = req.queryParams("contenido");
            String tipo = req.queryParams("tipo");

            String respuesta = "";

            if (tipo.equals("pdf")) {

                Document document = new Document();
                try {
                    PdfWriter.getInstance(document, new FileOutputStream("doc.pdf"));
                    Chunk chunk = new Chunk(titulo);
                    Chapter chapter = new Chapter(new Paragraph(chunk), 1);
                    chapter.setNumberDepth(0);
                    chapter.add(new Paragraph(contenido));
                    document.open();
                    document.add(chapter);
                    document.close();
                } catch (DocumentException ex) {
                    respuesta = ex.getMessage();
                }

                resp.header("Content-Type", "application/pdf");
                ByteArrayOutputStream baos = getByteArrayOutputStream("doc.pdf");

                baos.writeTo(resp.raw().getOutputStream());
                resp.raw().getOutputStream().flush();
            } else if (tipo.equals("html")) {
                respuesta += "<html>";
                respuesta += "<head>";
                respuesta += "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\"/>";
                respuesta += "</head>";
                respuesta += "<body class='container'>";
                respuesta += "<h1>" + titulo + "</h1>";
                respuesta += "<div class=\"panel panel-default\">";
                respuesta += "<div class=\"panel-body\">" + contenido + "</div>";
                respuesta += "</div?";
                respuesta += "</body>";
                respuesta += "</html>";
                
            }
            return respuesta;
            
        });
    }
    
    private static ByteArrayOutputStream getByteArrayOutputStream(String ruta) throws IOException {

        File file = new File(ruta);

        FileInputStream fis = new FileInputStream(file);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[256];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum); //no doubt here is 0
                //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                System.out.println("read " + readNum + " bytes,");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return bos;
    }
}
