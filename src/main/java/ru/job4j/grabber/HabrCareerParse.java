package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link){
        Elements rows;
        try {
            Document document = Jsoup.connect(link).get();
            rows = document.select(".vacancy-description__text");
        } catch (IOException e) {
            throw new IllegalArgumentException("Exception in method 'retrieveDescription()'");
        }
        return rows.text();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Connection connection = Jsoup.connect(String.format("%s%d", link, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Post post = new Post();
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                post.setLocalDateTime(dateTimeParser.parse(dateElement.attr("datetime")));
                post.setTitle(titleElement.text());
                post.setLink(String.format("%s%s", SOURCE_LINK, linkElement.attr("href")));
                post.setDescription(retrieveDescription(post.getLink()));
                posts.add(post);
            });
        }
        return posts;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse parse = new HabrCareerParse(new HabrCareerDateTimeParser());
        System.out.println(parse.list(PAGE_LINK));
    }
}