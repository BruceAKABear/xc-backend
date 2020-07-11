package net.zacard.xc.website.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-07-11 13:52
 */
@Data
@Document(collection = "user")
public class User implements Serializable {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String password;
}
