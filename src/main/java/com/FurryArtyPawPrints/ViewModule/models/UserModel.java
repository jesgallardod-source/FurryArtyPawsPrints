package com.FurryArtyPawPrints.ViewModule.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@EqualsAndHashCode
public class UserModel {

    @Id
    @Column("user_id")
    private Integer userId;

    @Column("user_name")
    private String userName;

    @Column("user_last_name")
    private String userLastName;

    @Column("user_email")
    private String userEmail;

    @Column("user_password")
    private String userPassword;

    @Column("user_location")
    private String userLocation;

    @Column("user_type")
    private String userType;

    @Column("user_preferences")
    private String userPreferences;

    @Column("n_commissions")
    private Integer NCommissions;

    @Column("user_com_date")
    private LocalDateTime userComDate;

    @Column("user_com_message")
    private String userComMessage;

    @Column("user_com_discount")
    private Integer userComDiscount;

    @Column("user_twitter")
    private String userTwitter;

    @Column("user_facebook")
    private String userFacebook;

    @Column("user_blue_sky")
    private String userBlueSky;

    @Column("user_patreon")
    private String userPatreon;
}
