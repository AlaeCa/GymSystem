package ma.ac.esi.gymsystem.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.FileOutputStream;

public class PdfService {

    public void genererFicheInscription(String nom, String prenom, String telephone, String dateAdhesion) {
        Document document = new Document();
        try {
            // Le nom du fichier sera basé sur le nom du membre
            String fileName = "Fiche_" + nom + "_" + prenom + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();

            // 1. En-tête
            Font fontTitre = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.BLUE);
            Paragraph titre = new Paragraph("GYMSYSTEM - FICHE D'INSCRIPTION", fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);
            document.add(new Chunk(new LineSeparator())); // Une ligne de séparation
            document.add(new Paragraph(" ")); // Espace vide

            // 2. Informations du Membre
            Font fontLabel = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("INFORMATIONS PERSONNELLES", fontLabel));
            document.add(new Paragraph("-----------------------------------------------------------"));
            document.add(new Paragraph("Nom : " + nom.toUpperCase()));
            document.add(new Paragraph("Prénom : " + prenom));
            document.add(new Paragraph("Téléphone : " + telephone));
            document.add(new Paragraph("Date d'adhésion : " + dateAdhesion));
            document.add(new Paragraph(" "));

            // 3. Règlement
            document.add(new Paragraph("ENGAGEMENT DU MEMBRE", fontLabel));
            document.add(new Paragraph("Le membre s'engage à respecter le règlement intérieur de la salle. "
                    + "Toute absence non justifiée aux cours collectifs pourra entraîner des frais."));
            document.add(new Paragraph(" "));

            // 4. Zone de Signature
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Signature du Membre : _________________________"));
            document.add(new Paragraph("Date : " + java.time.LocalDate.now()));

            document.close();
            System.out.println("Le PDF a été généré avec succès : " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}