package in.surajx.rssviewer.xml;

import android.util.Xml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser for http://www.indianote.asia RSS Feed
 * Created by surajx on 21/4/15.
 */
public class RSSFeedXMLParser {

    private static final String ns = null;

    public RSSFeed parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private RSSFeed readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        RSSFeed feed = new RSSFeed();
        parser.require(XmlPullParser.START_TAG, ns, XMLTagNames.ROOT_TAG);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            switch (name) {
                case XMLTagNames.ITEM_TAG:
                    feed.addItem(readItem(parser));
                    break;
                case XMLTagNames.CHANNEL:
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return feed;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private FeedItem readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, XMLTagNames.ITEM_TAG);
        String title;
        String description;
        String link;
        String image;
        FeedItem feedItem = new FeedItem();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case XMLTagNames.TITLE_TAG:
                    title = readTitle(parser);
                    feedItem.setItemTitle(title);
                    break;
                case XMLTagNames.DESCRIPTION_TAG:
                    String descriptionHTML = readDescriptionHTML(parser);
                    Document doc = Jsoup.parse(descriptionHTML);
                    feedItem.setItemDescription(doc.text());
                    String imageSrc = null;
                    try {
                        imageSrc = doc.getElementsByTag(XMLTagNames.IMAGE_TAG).first().attr(XMLTagNames.IMAGE_TAG_SRC_ATTRIBUTE);
                    } catch (NullPointerException ignored) {
                        System.out.println("No Image available");
                    }
                    if (null != imageSrc && !imageSrc.isEmpty())
                        feedItem.setItemImage(imageSrc);
                    break;
                case XMLTagNames.LINK_TAG:
                    link = readLink(parser);
                    feedItem.setItemLink(link);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return feedItem;
    }

    private String readDescriptionHTML(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, XMLTagNames.DESCRIPTION_TAG);
        String descriptionHTML = parser.nextText();
        parser.require(XmlPullParser.END_TAG, ns, XMLTagNames.DESCRIPTION_TAG);
        return descriptionHTML;
    }

    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, XMLTagNames.LINK_TAG);
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, XMLTagNames.LINK_TAG);
        return link;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, XMLTagNames.TITLE_TAG);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, XMLTagNames.TITLE_TAG);
        return title;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
