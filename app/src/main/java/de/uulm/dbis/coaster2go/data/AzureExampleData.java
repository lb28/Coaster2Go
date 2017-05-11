package de.uulm.dbis.coaster2go.data;


import android.content.Context;

import java.util.List;
import java.util.Random;

/** A class to fill the SQL Tables with some Example data and exampleReviews and WaitingTimes.
 *
 */
public class AzureExampleData {
    private Context context;
    private String username1 = "Peter Lustig", username2 = "Peter Unlustig", username3 = "Hans Peter",
            username4 = "Krümelmonster";
    private String userid1 = "Peter_Lustig_Id", userid2 = "Peter_Unlustig_Id", userid3 = "Hans_Peter_Id",
            userid4 = "Kekse";
    private Park europapark, legoland, handschuhwelt, pripyat;
    private Attraction bluefire, euromir, mulmul, foodloop, silverstar, poesidon, baaexpress, dancingdiggy,
            irishpub, rafting, atlantica, alpenexpress, alpenexpressvr, arthur, magiccinema, batavia,
            drachenbahn, legoshop, miniland, flammendeFaust, faeustchen;
    private Review testReview1, testReview2, testReview3, testReview4;
    private WaitingTime testTime1, testTime2, testTime3, testTime4;
    private String europaparkid, legolandid, handschuhweltid, pripyatid;
    String attractionid, parkId;
    String wait = ".";

    /** Konstructor
     *
     * @param context context
     */
    public AzureExampleData(Context context) {
        this.context = context;
    }

    /** Fills the Database with Parks, Attractions, Reviews and WaitingTimes
     *
     * @return true if succesfull
     */
    public boolean fillDatabase(){
        try{
            AzureDBManager dbManager = new AzureDBManager(context);

            //Some Test Parks
            legoland = new Park("Legoland", "Günzburg", "Vergnügungspark mit Themenbereichen, " +
                    "Miniaturwelt aus Legosteinen, Achterbahnen und Fahrgeschäften.", 48.426802, 10.300726,
                    "http://freizeitparkfun.de/Legoland%20Bilder%206000.jpg",
                    0, 0, "");
            pripyat = new Park("Pripyat", "Tschernobyl", "Derzeit geschlossen.", 51.408246, 30.055386,
                    "https://f1.blick.ch/img/incoming/origs3669981/9972533768-w1280-h960/tschernobyl00010.jpg",
                    0, 0, "");
            handschuhwelt = new Park("Handschuhwelt", "Bikini Bottom", "Luft anhalten für Unterwasserspass.", 11.644220, 165.376451,
                    "http://vignette2.wikia.nocookie.net/spongebobb/images/d/db/GloveWorld1.jpg/revision/latest?cb=20150101235050",
                    0, 0, "");
            europapark = new Park("Europa Park", "Rust", "Bester Freizeitpark der Welt:Großer Vergnügungspark mit " +
                    "Themenwelten, Fahrgeschäften für die ganze Familie & Live-Shows. Offen: Apr–Jan.", 48.266015, 7.721972,
                    "http://www.mehrdrauf.de/cm/sparkasse-staufen-breisach/images/Europa-Park/EP2016_300x200.jpg",
                    0, 0, "");
            legolandid = dbManager.createPark(legoland).getId();
            pripyatid =  dbManager.createPark(pripyat).getId();
            handschuhweltid = dbManager.createPark(handschuhwelt).getId();
            europaparkid = dbManager.createPark(europapark).getId();
            System.out.println(wait);
            wait = wait+".";
            //Some Test Park Reviews
            testReview1 = new Review(legolandid, "Peter Lustig", "Peter_Lustig_ID", 5, "Finde den Park lustig.");
            testReview2 = new Review(handschuhweltid, "Peter Lustig", "Peter_Lustig_ID", 5, "Finde den Park lustig.");
            testReview3 = new Review(europaparkid, "Peter Lustig", "Peter_Lustig_ID", 5, "Finde den Park lustig.");
            dbManager.createReview(testReview1, false);
            dbManager.createReview(testReview2, false);
            dbManager.createReview(testReview3, false);
            testReview1 = new Review(legolandid, "Peter Unlustig", "Peter_Unlustig_ID", 1, "Finde den Park nicht lustig.");
            testReview2 = new Review(handschuhweltid, "Peter Unlustig", "Peter_Unlustig_ID", 1, "Finde den Park nicht lustig.");
            testReview3 = new Review(pripyatid, "Peter Unlustig", "Peter_Unlustig_ID", 1, "Finde den Park nicht lustig.");
            dbManager.createReview(testReview1, false);
            dbManager.createReview(testReview2, false);
            dbManager.createReview(testReview3, false);
            testReview1 = new Review(legolandid, "Hans Peter", "Hans_Peter_Id", 3, "Geht so.");
            testReview2 = new Review(handschuhweltid, "Hans Peter", "Hans_Peter_Id", 4, "Spassig.");
            testReview3 = new Review(pripyatid, "Hans Peter", "Hans_Peter_Id", 1, "Zu sehr verstrahlt.");
            testReview4 = new Review(europaparkid, "Hans Peter", "Hans_Peter_Id", 5, "Bester Park!");
            dbManager.createReview(testReview1, false);
            dbManager.createReview(testReview2, false);
            dbManager.createReview(testReview3, false);
            dbManager.createReview(testReview4, false);
            testReview1 = new Review(legolandid, "Krümelmonster", "Kekse", 2, "Toll aber zu wenig Kekse!");
            testReview2 = new Review(handschuhweltid, "Krümelmonster", "Kekse", 3, "Toll aber zu wenig Kekse!");
            testReview3 = new Review(pripyatid, "Krümelmonster", "Kekse", 1, "Toll aber zu wenig Kekse!.");
            testReview4 = new Review(europaparkid, "Krümelmonster", "Kekse", 4, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, false);
            dbManager.createReview(testReview2, false);
            dbManager.createReview(testReview3, false);
            dbManager.createReview(testReview4, false);
            System.out.println(wait);
            wait = wait+".";
            //Europapark Attractions and Reviews:
            euromir = new Attraction("Euro Mir", "Achterbahn", "Die Euro-Mir ist eine Achterbahn," +
                    " die im Europa-Park in Rust steht. Sie steht im russischen Themenbereich und wurde 1997 " +
                    "nach dreijähriger Bauzeit eröffnet.\n" +
                    "Höhe:28 m\n" +
                    "Höchstgeschwindigkeit:80 km/h\n" +
                    "Züge:9 Züge, 4 Wagen/Zug, 2 Sitzreihen/Wagen,", 48.264880, 7.719965,
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Euro-Mir_in_Europa-Park_Rust.JPG/220px-Euro-Mir_in_Europa-Park_Rust.JPG",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(euromir).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 5, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 5, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 5, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            bluefire = new Attraction("Blue Fire", "Achterbahn", "Der blue fire Megacoaster powered " +
                    "by GAZPROM im Europa-Park ist eine Stahlachterbahn vom Modell Launched Coaster – " +
                    "Mega Coaster des Herstellers Mack Rides, die am 4. April 2009 eröffnet wurde.\n" +
                    "Max. Beschleunigung:3,8 g\n" +
                    "Höchstgeschwindigkeit:100 km/h\n" +
                    "g-Kraft:3,8 g", 48.262391, 7.718368,
                    "http://www.gazprom.de/f/posts/49/836540/katapultachterbahn-blue-fire.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(bluefire).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 2, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 4, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            foodloop = new Attraction("FoodLoop", "Restuarant", "Essen wird hier auf Achterbahnen " +
                    "mit Looping zu den Gästen gebracht", 48.264373, 7.720820,
                    "https://media.holidaycheck.com/data/urlaubsbilder/mittel/41/1162565089.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(foodloop).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 4, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 3, "Toll aber zu wenig Kekse!");
            mulmul = new Attraction("Mül Müls Karussell", "Kinderbahn", "Kleines Karussell für Kinder",
                    48.264128, 7.724011,
                    "http://www.ep-fans.info/bilder/4283/1418589009.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(mulmul).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 1, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 5, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            silverstar = new Attraction("Silverstar", "Achterbahn", "Der Silver Star im Europa-Park in Rust bei " +
                    "Freiburg im Breisgau ist zusammen mit dem Schwur des Kärnan im Hansa-Park die " +
                    "höchste Achterbahn Deutschlands und hinter Shambhala aus Port Aventura die " +
                    "zweithöchste europäische Achterbahn.",
                    48.267914, 7.720020,
                    "http://www.europapark.de/sites/default/files/styles/gallery/public/Attraktionen/Sommer/Silverstar_1920_AT_Europa-Park-07_0.jpg?itok=UhV1YBEc",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(silverstar).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 2, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 5, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            poesidon = new Attraction("Poesidon", "Wasserbahn", "Poseidon ist eine Wasserachterbahn " +
                    "vom Modell Water Coaster des Herstellers Mack Rides im Europa-Park in Rust bei Freiburg im Breisgau.",
                    48.266600, 7.719337,
                    "http://www.freizeitpark-welt.de/freizeitparks/europa_park/fotos/2007/poseidon09.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(poesidon).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 2, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 4, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            baaexpress = new Attraction("Baa-a-aExpress", "Kinderbahn", "Eine kleine Achterbahn für Kinder.",
                    48.266298, 7.723189,
                    "http://www.parkplanet.nl/images/artikelen/du_europa_park/Kinderland/EP_Baa_Express_16pers.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(baaexpress).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 5, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 5, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            dancingdiggy = new Attraction("Dancing Diggy", "Action", "Schifffahrt.",
                    48.266298, 7.723189,
                    "http://www.europapark.de/sites/default/files/styles/gallery/public/Attraktionen/Dancing-Dingie_Attraktion_Europa-Park_03.jpg?itok=wjHOvoiQ",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(dancingdiggy).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 4, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 1, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            irishpub = new Attraction("Irish Pub", "Restaurant", "Typsiches irisches Pup mit frischem Guinness.",
                    48.266267, 7.723727,
                    "http://www.epfans.info/bilder/4582/1488308085.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(irishpub).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 5, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 5, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            System.out.println(wait);
            wait = wait+".";
            rafting = new Attraction("Fjordrafting", "Wasserbahn", "Hier wird jeder nass!",
                    48.262243, 7.720417,
                    "https://farm3.staticflickr.com/2479/5710403212_3fa2e0767b_z.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(rafting).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 2, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 1, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            atlantica = new Attraction("Atlantica Supersplash", "Wasserbahn", "Hier wird fast jeder nass!",
                    48.261649, 7.721175,
                    "https://media.glassdoor.com/o/04/82/c7/d2/europa-park-atlantica-supersplash-photo-thanks-to-wikipedia-commons.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(atlantica).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 3, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 2, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            alpenexpress = new Attraction("Alpenexpress", "Achterbahn", "Der Alpenexpress „Enzian“ " +
                    "ist eine angetriebene Achterbahn vom Typ Blauer Enzian des Herstellers Mack Rides " +
                    "im Europa-Park in Rust. Sie steht im Themenbereich „Österreich“, wurde 1984 eröffnet " +
                    "und war die erste Achterbahn im Europapark.\n" +
                    "Höchstgeschwindigkeit:45 km/h\n" +
                    "Dauer:1 Minute 40 Sekunden\n" +
                    "Eröffnung:1984",
                    48.262058, 7.722647,
                    "http://planetinsight.de/wp-content/uploads/2014/09/243_Europapark_8-Kopie.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(alpenexpress).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 4, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 4, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            alpenexpressvr = new Attraction("Alpenexpress VR", "Action", "Fahrt auf der Achterbahn mit VR-Brillen.",
                    48.262058, 7.722647,
                    "http://gaminggadgets.de/wp-content/uploads/2015/09/alpenexpress-vr-ride.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(alpenexpressvr).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 4, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 4, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            arthur = new Attraction("Arthur", "Rundfahrt", "Arthur im Europa-Park im " +
                    "baden-württembergischen Rust ist ein Stahlachterbahn-Darkride-Hybrid vom Modell " +
                    "Inverted Powered Coaster des Herstellers Mack Rides, der am 18. September 2014 " +
                    "offiziell eröffnet wurde.\n" +
                    "Höchstgeschwindigkeit:31 km/h\n" +
                    "Kosten:ca. 25 Mio. Euro\n" +
                    "Eröffnung:18. September 2014",
                    48.263940, 7.723951,
                    "http://www.freizeitparkfun.de/Europa-Park-arthur-im-koenigreich-der-minimoys-101.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(arthur).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 4, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 4, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 4, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            magiccinema = new Attraction("Magic Cinema 4D", "Show", "Nachmittags 4D Filme. Abends wird " +
                    "das interaktive Erlebniskino im Freizeitpark zum öffentlichen Filmtheater für neue Blockbuster. ",
                    48.267989, 7.721594,
                    "http://www.freizeitpark-welt.de/freizeitparks/europa_park/fotos/2012/schloss_balthasar01.jpg",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(magiccinema).getId();
            testReview1 = new Review(attractionid, username1, userid1, 3, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 4, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 3, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 4, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            System.out.println(wait);
            wait = wait+".";
            batavia = new Attraction("Piraten in Batavia", "Rundfahrt", "Piraten in Batavia ist ein " +
                    "Wasser-Dark-Ride im Europa-Park in Rust. Die Anlage des Herstellers Mack Rides, " +
                    "der sich im Besitz der Eigentümer des Europa-Park befindet, wurde im Jahr 1987 errichtet.\n" +
                    "Anzahl Boote:22\n" +
                    "Personen pro Boot:16\n" +
                    "Eröffnung:1987",
                    48.263560, 7.720369,
                    "http://www.europapark.de/sites/default/files/styles/gallery/public/Attraktionen/Sommer/Indoor-Fun_Piraten-in-Batavia_Sommer_Europa-Park-04.jpg?itok=L4fGlKdX",
                    0, 0, 0, 0, 0, 0, 0, europaparkid);
            attractionid = dbManager.createAttraction(batavia).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 4, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 2, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 3, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            //Legoland Attractions and Reviews:
            drachenbahn = new Attraction("Drachenbahn", "Achterbahn", "Drachenbahn: Fahrt mit dem Drachen und durch die Burg.",
                    48.425594, 10.302924,
                    "https://oldlivetvgcms.e-confirm.de/Docs/User/id_-3/img/L%C3%A4nderspecial/Freizeitparks/LEGOLAND/BAY_Legoland_024.jpg",
                    0, 0, 0, 0, 0, 0, 0, legolandid);
            attractionid = dbManager.createAttraction(drachenbahn).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 4, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 2, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            legoshop = new Attraction("Lego Shop", "Shop", "Viele Legos zum Kaufen.",
                    48.426802, 10.300726,
                    "http://www.tamerholding.com/Library/Images/%20lego.jpg",
                    0, 0, 0, 0, 0, 0, 0, legolandid);
            attractionid = dbManager.createAttraction(legoshop).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 2, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 3, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            miniland = new Attraction("Miniland", "Laufattraktion", "Miniaturwelten aus Legosteinen.",
                    48.424139, 10.301156,
                    "https://endpoint913813.azureedge.net/globalassets/entdecken/themenwelten/miniland/allianz-arena/uvid-4affd0/allianz-arena-legoland.png",
                    0, 0, 0, 0, 0, 0, 0, legolandid);
            attractionid = dbManager.createAttraction(miniland).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 5, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 5, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            //Handschuhwelt Attractions and Reviews:
            flammendeFaust = new Attraction("Flammende Faust des Schmerzes", "Achterbahn", "Die Fahrt verursacht eventuell:\n" +
                    "Heulen\n" +
                    "Schreien\n" +
                    "Stoßartiges Erbrechen\n" +
                    "Amnesie\n" +
                    "Grätenverlust\n" +
                    "Unangenehme Unfälle\n" +
                    "Blähungen\n" +
                    "und explosive Durchfallattacken",
                    11.644220, 165.376451,
                    "http://de.spongepedia.org/images/86a_Flammende_Faust_des_Schmerzes.jpg",
                    0, 0, 0, 0, 0, 0, 0, handschuhweltid);
            attractionid = dbManager.createAttraction(flammendeFaust).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 4, "Trivial.");
            testReview4 = new Review(attractionid, username4, userid4, 4, "Toll aber zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            faeustchen = new Attraction("Fäustchen", "Achterbahn", "Es handelt sich hierbei um eine " +
                    "winzige Bahn aus grauen Schienen, in der man im Kreis fährt. Sie wird von einem" +
                    " schlecht gelaunten Angestellten mit einem Schalter bedient wird und der einziger" +
                    " Höhepunkt ist, dass ein spärlicher Hügel überwunden werden muss. ",
                    11.644220, 165.376451,
                    "http://de.spongepedia.org/images/Das_F%C3%A4ustchen.jpg",
                    0, 0, 0, 0, 0, 0, 0, handschuhweltid);
            attractionid = dbManager.createAttraction(faeustchen).getId();
            testReview1 = new Review(attractionid, username1, userid1, 5, "Ich fand es lustig!");
            testReview2 = new Review(attractionid, username2, userid2, 1, "Ich fand es nicht lustig!");
            testReview3 = new Review(attractionid, username3, userid3, 1, "Zu Brutal!");
            testReview4 = new Review(attractionid, username4, userid4, 1, "Brutal und zu wenig Kekse!");
            dbManager.createReview(testReview1, true);
            dbManager.createReview(testReview2, true);
            dbManager.createReview(testReview3, true);
            dbManager.createReview(testReview4, true);
            System.out.println(wait);
            wait = wait+".";

            fillDatabaseWaitingTimes();

            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /** Adds some new WaitingTimes into the database for all Attractions
     *
     * @return true if succesfull
     */
    public boolean fillDatabaseWaitingTimes(){
        try{
            AzureDBManager dbManager = new AzureDBManager(context);
            Random r = new Random();
            int counter = 0;
            int random = 0;
            //Create some new random WaitingTimes for all Attractions in all Parks and fill database
            List<Park> parkList = dbManager.getParkList();
            for(Park p : parkList){
                parkId = p.getId();
                List<Attraction> attractionList = dbManager.getAttractionList(parkId);
                for(Attraction a : attractionList){
                    attractionid = a.getId();
                    random = counter%4;
                    switch (random){
                        case 0:
                            testTime1 = new WaitingTime(attractionid, username1, userid1, r.nextInt(99));
                            dbManager.createWaitingTime(testTime1);
                            break;
                        case 1:
                            testTime2 = new WaitingTime(attractionid, username2, userid2, r.nextInt(99));
                            dbManager.createWaitingTime(testTime2);
                            break;
                        case 2:
                            testTime3 = new WaitingTime(attractionid, username3, userid3, r.nextInt(120));
                            dbManager.createWaitingTime(testTime3);
                        default:
                            testTime4 = new WaitingTime(attractionid, username4, userid4, r.nextInt(45));
                            dbManager.createWaitingTime(testTime4);
                            break;
                    }
                    counter++;
                }
                System.out.println(wait);
                wait = wait+".";
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
