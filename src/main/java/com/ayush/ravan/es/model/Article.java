//package com.ayush.ravan.es.model;
//
//import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
//import static org.springframework.data.elasticsearch.annotations.FieldType.Nested;
//import static org.springframework.data.elasticsearch.annotations.FieldType.Text;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.InnerField;
//import org.springframework.data.elasticsearch.annotations.MultiField;
//import org.springframework.data.annotation.Id;
//
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.List;
//
//
//@Document(indexName = "#{esConfig.indexName}", type = "#{esConfig.indexType}")
//public class Article implements Serializable {
//    @Id
//    @GeneratedValue(strategy= GenerationType.AUTO)
//    private String id;
//
//    @Field(type = FieldType.text)
//    private String name;
//
//    @Field(type = FieldType.text)
//    private String summary;
//
//    @Field(type = FieldType.Integer)
//    private Integer price;
//
//
//    @MultiField(mainField = @Field(type = Text, fielddata = true), otherFields = { @InnerField(suffix = "verbatim", type = Keyword) })
//    private String title;
//
//    @Field(type = Nested, includeInParent = true)
//    private List<Author> authors;
//
//    @Field(type = FieldType.Object)
//    private Department department;
//
//    @Field(type = Keyword)
//    private String[] tags;
//
//    @Field(type = FieldType.Keyword)
//    private Category category;
//
//    public enum Category {
//        CLOTHES,
//        ELECTRONICS,
//        GAMES;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getSummary() {
//        return summary;
//    }
//
//    public void setSummary(String summary) {
//        this.summary = summary;
//    }
//
//    public Integer getPrice() {
//        return price;
//    }
//
//    public void setPrice(Integer price) {
//        this.price = price;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public List<Author> getAuthors() {
//        return authors;
//    }
//
//    public void setAuthors(List<Author> authors) {
//        this.authors = authors;
//    }
//
//    public String[] getTags() {
//        return tags;
//    }
//
//    public void setTags(String[] tags) {
//        this.tags = tags;
//    }
//
//    public Category getCategory() {
//        return category;
//    }
//
//    public void setCategory(Category category) {
//        this.category = category;
//    }
//
//    @Override
//    public String toString() {
//        return "Article{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", summary='" + summary + '\'' +
//                ", price=" + price +
//                ", title='" + title + '\'' +
//                ", authors=" + authors +
//                ", tags=" + Arrays.toString(tags) +
//                ", category=" + category +
//                '}';
//    }
//}
