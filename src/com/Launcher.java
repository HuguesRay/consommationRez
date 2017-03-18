package com;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Launcher {

	public Launcher(int phase, int appart, int threshold) {
		System.out.println("phase: "+phase+" appartement #: "+appart);
		
		Calendar c = Calendar.getInstance();
		int cMonth = c.get(Calendar.MONTH)+1;
		
		//query for the current month only
		String url = "http://www2.cooptel.qc.ca/services/temps/?mois="+cMonth+"&cmd=Visualiser";
        try {
        	
        	String user = "ets-res"+phase+"-"+appart;
        	String password = "ets"+appart;
        	String login = user + ":" + password;
        	String base64login = new String(Base64.encodeBase64(login.getBytes()));
        	
			Document document = Jsoup.connect(url)
					.header("Authorization", "Basic " + base64login)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.143 Safari/537.36")
					.get();
			
			String usedBandwith = document.select("td:contains(Total combiné (Go):)").last().nextElementSibling().text();
			String totalBandwith = document.select("td:contains(Quota permis pour la période (Go))").last().nextElementSibling().text();
			
			float remainingBandwith = Float.parseFloat(totalBandwith) - Float.parseFloat(usedBandwith);
			if(remainingBandwith < threshold) {
				sendNotification();
			}
			
			System.out.println("Remaining bandwith: "+remainingBandwith+" GB / "+totalBandwith + " GB");

			
		} catch (IOException e) {
			System.out.println("Cooptel ne peut pas etre joint en ce moment ou l'appartement n'existe pas");
		}
		
		System.exit(0);
	}

	private void sendNotification() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		//new Launcher(4,639,15);
		new Launcher(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
	}

}
