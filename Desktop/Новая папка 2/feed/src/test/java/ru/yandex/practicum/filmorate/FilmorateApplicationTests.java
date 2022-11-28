package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dal.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmGenreLineStorage filmGenreLineStorage;
    private final FilmStorage filmStorage;
    private final FriendsStorage friendsStorage;
    private final GenreStorage genreStorage;
    private final LikesStorage likesStorage;
    private final MpaStorage mpaStorage;
    private final ReviewStorage reviewStorage;
    private final ReviewRatingStorage reviewRatingStorage;
    private final DirectorStorage directorStorage;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM REVIEW_RATING");
        jdbcTemplate.update("DELETE FROM LIKES");
        jdbcTemplate.update("DELETE FROM FILM_GENRE_LINE");
        jdbcTemplate.update("DELETE FROM FRIENDS");
        jdbcTemplate.update("DELETE FROM REVIEWS");
        jdbcTemplate.update("DELETE FROM FILM_DIRECTOR_LINE");
        jdbcTemplate.update("DELETE FROM DIRECTORS");
        jdbcTemplate.update("DELETE FROM REVIEWS");
        jdbcTemplate.update("DELETE FROM USERS");
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("ALTER TABLE DIRECTORS ALTER COLUMN DIRECTOR_ID RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE REVIEWS ALTER COLUMN REVIEW_ID RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1");
    }

    @Test
    void addUserTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User newUser = userStorage.addUser(user);
        user.setId(1);
        assertThat(user, equalTo(newUser));
    }

    @Test
    void updateUserTest() {
        User user1 = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User oldUser = userStorage.addUser(user1);
        User user2 = User.builder()
                .id(oldUser.getId())
                .email("newUser2@yandex.ru")
                .login("newLoginUser2022")
                .name("NewUser")
                .birthday(LocalDate.of(1990, 1, 2))
                .friends(new ArrayList<>())
                .build();
        User updateUser = userStorage.updateUser(user2);
        assertThat("Пользователь не обновлен", user2, equalTo(updateUser));
    }

    @Test
    void updateUserFailTest() {
        User user = User.builder()
                .id(999)
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        ObjectNotFoundException e = Assertions.assertThrows(
                ObjectNotFoundException.class, () -> userStorage.updateUser(user));
        assertThat("User with id 999 not found", equalTo(e.getMessage()));
    }

    @Test
    void getUsersByEmptyTest() {
        Collection<User> users = userStorage.getUsers();
        assertThat("Список пользователей не пуст", users, empty());
    }

    @Test
    void getUsersTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        assertThat("Список пользователей пуст", userStorage.getUsers(), hasSize(2));
        assertThat("User1 не найден", userStorage.getUsers(), hasItem(addUser1));
        assertThat("User2 не найден", userStorage.getUsers(), hasItem(addUser2));
    }

    @Test
    void getUserInvalidIdTest() {
        ObjectNotFoundException e = Assertions.assertThrows(
                ObjectNotFoundException.class, () -> userStorage.getUserById(1));
        assertThat("User with id 1 not found", equalTo(e.getMessage()));
    }

    @Test
    void getUserById() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User addUser = userStorage.addUser(user1);
        assertThat(addUser, equalTo(userStorage.getUserById(addUser.getId())));
    }

    @Test
    void getFriendsByEmptyTest() {
        Collection<Long> friends = friendsStorage.getListOfFriends(1);
        assertThat("Список друзей не пуст", friends, hasSize(0));
    }

    @Test
    void addAsFriendTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        friendsStorage.addAsFriend(addUser1.getId(), addUser2.getId());
        assertThat("User2 не добавлен в друзья User1",
                userStorage.getUserById(addUser1.getId()).getFriends(), hasItem(addUser2.getId()));
        assertThat("Список друзей User2 не пуст",
                userStorage.getUserById(addUser2.getId()).getFriends(), empty());
    }

    @Test
    void removeFromFriendsAsFriendTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        friendsStorage.addAsFriend(addUser1.getId(), addUser2.getId());
        assertThat("Список друзей User1 пуст",
                userStorage.getUserById(addUser1.getId()).getFriends(), hasItem(addUser2.getId()));
        friendsStorage.removeFromFriends(addUser1.getId(), addUser2.getId());
        assertThat("Список друзей User1 не пуст",
                userStorage.getUserById(addUser1.getId()).getFriends(), empty());
    }

    @Test
    void getListOfFriendsTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1992, 1, 1))
                .build();
        User user3 = User.builder()
                .email("user3@yandex.ru")
                .login("user3")
                .name("User3")
                .birthday(LocalDate.of(1993, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        User addUser3 = userStorage.addUser(user3);
        friendsStorage.addAsFriend(addUser1.getId(), addUser2.getId());
        friendsStorage.addAsFriend(addUser1.getId(), addUser3.getId());
        assertThat("Список друзей User1 не содержит id User2 и User3",
                friendsStorage.getListOfFriends(addUser1.getId()), contains(addUser2.getId(), addUser3.getId()));
    }

    @Test
    void getAListOfMutualFriendsTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1992, 1, 1))
                .build();
        User user3 = User.builder()
                .email("user3@yandex.ru")
                .login("user3")
                .name("User3")
                .birthday(LocalDate.of(1993, 1, 1))
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        User addUser3 = userStorage.addUser(user3);
        friendsStorage.addAsFriend(addUser1.getId(), addUser3.getId());
        friendsStorage.addAsFriend(addUser2.getId(), addUser3.getId());
        assertThat("Список друзей User1 не содержит id User2 и User3",
                friendsStorage.getAListOfMutualFriends(addUser1.getId(), addUser2.getId()),
                contains(addUser3.getId()));
    }

    @Test
    void addFilmTest() {
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        film.setId(1);
        assertThat(film, equalTo(addFilm));
    }

    @Test
    void updateFilmTest() {
        Film film1 = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .directors(List.of(Director.builder().name("Мэри Хэррон").build()))
                .build();
        Film oldFilm = filmStorage.addFilm(film1);
        Film film2 = Film.builder()
                .id(oldFilm.getId())
                .name("newPsycho1")
                .description("newАмериканский психологический хоррор 1960 года.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(5)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                .build();
        Film updateFilm = filmStorage.updateFilm(film2);
        assertThat("Фильм не обновлен", film2, equalTo(updateFilm));
    }

    @Test
    void updateFilmFailTest() {
        Film film = Film.builder()
                .id(999)
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .directors(List.of(Director.builder().name("Мэри Хэррон").build()))
                .build();
        ObjectNotFoundException e = Assertions.assertThrows(
                ObjectNotFoundException.class, () -> filmStorage.updateFilm(film));
        assertThat("Film with id 999 not found", equalTo(e.getMessage()));
    }

    @Test
    void getFilmsByEmptyTest() {
        Collection<Film> films = filmStorage.getFilms();
        assertThat("Список фильмов не пуст", films, empty());
    }

    @Test
    void getFilmsTest() {
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film film2 = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(1961, 1, 1))
                .duration(109)
                .rate(5)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm1 = filmStorage.addFilm(film1);
        Film addFilm2 = filmStorage.addFilm(film2);
        assertThat("Список пользователей пуст", filmStorage.getFilms(), hasSize(2));
        assertThat("Film1 не найден", filmStorage.getFilms(), hasItem(addFilm1));
        assertThat("Film2 не найден", filmStorage.getFilms(), hasItem(addFilm2));
    }

    @Test
    void getFilmInvalidIdTest() {
        ObjectNotFoundException e = Assertions.assertThrows(
                ObjectNotFoundException.class, () -> filmStorage.getFilmById(1));
        assertThat("Film with id 1 not found", equalTo(e.getMessage()));
    }

    @Test
    void getFilmById() {
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film addFilm = filmStorage.addFilm(film1);
        assertThat(addFilm, equalTo(filmStorage.getFilmById(addFilm.getId())));
    }

    @Test
    void getLikesByEmptyTest() {
        Collection<Long> likes = likesStorage.getListOfLikes(1);
        assertThat("Список лайков не пуст", likes, hasSize(0));
    }

    @Test
    void addLikeTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        User addUser1 = userStorage.addUser(user1);
        Film addFilm1 = filmStorage.addFilm(film1);
        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("%s не поставил лайк %s", addUser1.getName(), addFilm1.getName()),
                filmStorage.getFilmById(addFilm1.getId()).getLikes(), hasItem(addUser1.getId()));
    }

    @Test
    void unlikeTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        User addUser1 = userStorage.addUser(user1);
        Film addFilm1 = filmStorage.addFilm(film1);
        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("Список лайков %s пуст", addFilm1.getName()),
                filmStorage.getFilmById(addFilm1.getId()).getLikes(), hasItem(addUser1.getId()));
        likesStorage.unlike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("Список лайков %s не пуст", addFilm1.getName()),
                filmStorage.getFilmById(addFilm1.getId()).getLikes(), empty());
    }

    @Test
    void getListOfLikes() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        User addUser1 = userStorage.addUser(user1);
        Film addFilm1 = filmStorage.addFilm(film1);
        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        assertThat(String.format("Список лайков %s не содержит id %s = %s",
                        addFilm1.getName(), addUser1.getName(), addUser1.getId()),
                likesStorage.getListOfLikes(addFilm1.getId()), contains(addUser1.getId()));
    }

    @Test
    void getTheBestFilmsTest() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1992, 1, 1))
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film film2 = Film.builder()
                .name("Film2")
                .description("Description2")
                .releaseDate(LocalDate.of(1961, 1, 1))
                .duration(109)
                .rate(5)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        User addUser1 = userStorage.addUser(user1);
        User addUser2 = userStorage.addUser(user2);
        Film addFilm1 = filmStorage.addFilm(film1);
        Film addFilm2 = filmStorage.addFilm(film2);
        likesStorage.addLike(addFilm1.getId(), addUser1.getId());
        likesStorage.addLike(addFilm1.getId(), addUser2.getId());
        assertThat("Список лучших фильмов отличается от [1, 2]",
                likesStorage.getTheBestFilms(5), contains(addFilm1.getId(), addFilm2.getId()));
        assertThat("Список лучших фильмов отличается от [1]",
                likesStorage.getTheBestFilms(1), hasItem(addFilm1.getId()));
    }

    @Test
    void getGenresTest() {
        Genre genre = Genre.builder()
                .id(6)
                .name("Боевик")
                .build();
        assertThat(genreStorage.getGenres(), hasSize(6));
        assertThat(genreStorage.getGenres(), hasItem(genre));
    }

    @Test
    void getGenreByIdTest() {
        Genre genre1 = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        Genre genre6 = Genre.builder()
                .id(6)
                .name("Боевик")
                .build();
        assertThat(genreStorage.getGenreById(1), equalTo(genre1));
        assertThat(genreStorage.getGenreById(6), equalTo(genre6));
    }

    @Test
    void addGenreTest() {
        Genre genre1 = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film addFilm1 = filmStorage.addFilm(film1);
        filmGenreLineStorage.addGenres(List.of(genre1), addFilm1.getId());
        assertThat(filmStorage.getFilmById(addFilm1.getId()).getGenres(), hasItem(genre1));
    }

    @Test
    void getListOfGenresTest() {
        Genre genre2 = Genre.builder()
                .id(2)
                .name("Драма")
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(genre2))
                .build();
        Film addFilm1 = filmStorage.addFilm(film1);
        assertThat(filmGenreLineStorage.getListOfGenres(addFilm1.getId()), hasItem(genre2.getId()));
    }

    @Test
    void deleteGenreTest() {
        Genre genre2 = Genre.builder()
                .id(2)
                .name("Драма")
                .build();
        Film film1 = Film.builder()
                .name("Film1")
                .description("Description1")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(genre2))
                .build();
        Film addFilm1 = filmStorage.addFilm(film1);
        filmGenreLineStorage.deleteGenres(addFilm1.getId());
        assertThat(filmGenreLineStorage.getListOfGenres(addFilm1.getId()), empty());
    }

    @Test
    void getMpaTest() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        assertThat(mpaStorage.getMpa(), hasSize(5));
        assertThat(mpaStorage.getMpa(), hasItem(mpa));
    }

    @Test
    void getMpaByIdTest() {
        Mpa mpa1 = Mpa.builder()
                .id(1)
                .name("G")
                .build();
        Mpa mpa5 = Mpa.builder()
                .id(5)
                .name("NC-17")
                .build();
        assertThat(mpaStorage.getMpaById(1), equalTo(mpa1));
        assertThat(mpaStorage.getMpaById(5), equalTo(mpa5));
    }

    @Test
    void addReviewTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser = userStorage.addUser(user);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        Review review = Review.builder()
                .content("Классный фильм")
                .isPositive(true)
                .userId(addUser.getId())
                .filmId(addFilm.getId())
                .build();
        Review addReview = reviewStorage.addReview(review);
        review.setReviewId(1);
        assertThat(review, equalTo(addReview));
    }

    @Test
    void updateReviewTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser = userStorage.addUser(user);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        Review review = Review.builder()
                .content("Классный фильм")
                .isPositive(true)
                .userId(addUser.getId())
                .filmId(addFilm.getId())
                .build();
        Review addReview = reviewStorage.addReview(review);
        Review newReview = Review.builder()
                .reviewId(1)
                .content("Плохой фильм")
                .isPositive(false)
                .userId(addUser.getId())
                .filmId(addFilm.getId())
                .build();
        Review updateReview = reviewStorage.updateReview(newReview);
        assertThat(newReview, equalTo(updateReview));
    }

    @Test
    void updateSomeoneElseIsReviewTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser = userStorage.addUser(user);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        Review review = Review.builder()
                .content("Классный фильм")
                .isPositive(true)
                .userId(addUser.getId())
                .filmId(addFilm.getId())
                .build();
        Review addReview = reviewStorage.addReview(review);
        Review newReview = Review.builder()
                .reviewId(1)
                .content("Плохой фильм")
                .isPositive(false)
                .userId(2L)
                .filmId(2L)
                .build();
        Review updateReview = reviewStorage.updateReview(newReview);
        assertThat(newReview.getContent(), equalTo(updateReview.getContent()));
        assertThat(newReview.getIsPositive(), equalTo(updateReview.getIsPositive()));
        assertThat(addReview.getUserId(), equalTo(updateReview.getUserId()));
        assertThat(addReview.getFilmId(), equalTo(updateReview.getFilmId()));
    }

    @Test
    void removeReviewTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser = userStorage.addUser(user);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        Review review = Review.builder()
                .content("Классный фильм")
                .isPositive(true)
                .userId(addUser.getId())
                .filmId(addFilm.getId())
                .build();
        Review addReview = reviewStorage.addReview(review);
        assertThat(reviewStorage.getReviews(addFilm.getId(), 10), hasItem(addReview));
        reviewStorage.removeReview(addReview.getReviewId());
        assertThat(reviewStorage.getReviews(addFilm.getId(), 10), empty());
    }

    @Test
    void getReviewByIdTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser = userStorage.addUser(user);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        Review review = Review.builder()
                .content("Классный фильм")
                .isPositive(true)
                .userId(addUser.getId())
                .filmId(addFilm.getId())
                .build();
        Review addReview = reviewStorage.addReview(review);
        assertThat(reviewStorage.getReviewById(addReview.getReviewId()), equalTo(addReview));
    }

    @Test
    void getReviewByIdFailTest() {
        ObjectNotFoundException e = Assertions.assertThrows(
                ObjectNotFoundException.class, () -> reviewStorage.getReviewById(1));
        assertThat("Review with id 1 not found", equalTo(e.getMessage()));
    }

    @Test
    void getReviewsTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser = userStorage.addUser(user);
        Film film1 = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm1 = filmStorage.addFilm(film1);
        Film film2 = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm2 = filmStorage.addFilm(film2);
        Review review1 = Review.builder()
                .content("Классный фильм")
                .isPositive(true)
                .userId(addUser.getId())
                .filmId(addFilm1.getId())
                .build();
        Review addReview1 = reviewStorage.addReview(review1);
        Review review2 = Review.builder()
                .content("Плохой фильм")
                .isPositive(false)
                .userId(addUser.getId())
                .filmId(addFilm2.getId())
                .build();
        Review addReview2 = reviewStorage.addReview(review2);
        assertThat(reviewStorage.getReviews(0, 10), hasSize(2));
        assertThat(reviewStorage.getReviews(addFilm1.getId(), 10), hasSize(1));
        assertThat(reviewStorage.getReviews(addFilm1.getId(), 10), hasItem(addReview1));
    }

    @Test
    void addAndRemoveLikeTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser = userStorage.addUser(user);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        Review review = Review.builder()
                .content("Классный фильм")
                .isPositive(true)
                .userId(addUser.getId())
                .filmId(addFilm.getId())
                .build();
        Review addReview = reviewStorage.addReview(review);
        reviewRatingStorage.addLikeDislike(addReview.getReviewId(), addUser.getId(), true);
        assertThat(1L, equalTo(reviewRatingStorage.getReviewRating(addReview.getReviewId())));
        reviewRatingStorage.removeLikeDislike(addReview.getReviewId(), addUser.getId(), true);
        assertThat(0L, equalTo(reviewRatingStorage.getReviewRating(addReview.getReviewId())));
    }

    @Test
    void addAndRemoveDislikeTest() {
        User user = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser = userStorage.addUser(user);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        Review review = Review.builder()
                .content("Классный фильм")
                .isPositive(true)
                .userId(addUser.getId())
                .filmId(addFilm.getId())
                .build();
        Review addReview = reviewStorage.addReview(review);
        reviewRatingStorage.addLikeDislike(addReview.getReviewId(), addUser.getId(), false);
        assertThat(-1L, equalTo(reviewRatingStorage.getReviewRating(addReview.getReviewId())));
        reviewRatingStorage.removeLikeDislike(addReview.getReviewId(), addUser.getId(), false);
        assertThat(0L, equalTo(reviewRatingStorage.getReviewRating(addReview.getReviewId())));
    }

    @Test
    void getReviewRatingTest() {
        User user1 = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User1")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser1 = userStorage.addUser(user1);
        User user2 = User.builder()
                .email("user@yandex.ru")
                .login("loginUser2022")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new ArrayList<>())
                .build();
        User addUser2 = userStorage.addUser(user2);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film addFilm = filmStorage.addFilm(film);
        Review review = Review.builder()
                .content("Классный фильм")
                .isPositive(true)
                .userId(addUser1.getId())
                .filmId(addFilm.getId())
                .build();
        Review addReview = reviewStorage.addReview(review);
        reviewRatingStorage.addLikeDislike(addReview.getReviewId(), addUser1.getId(), true);
        reviewRatingStorage.addLikeDislike(addReview.getReviewId(), addUser2.getId(), false);
        assertThat(0L, equalTo(reviewRatingStorage.getReviewRating(addReview.getReviewId())));
    }

    @Test
    void getRecommendationsIfNoLikes() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();

        User user1Added = userStorage.addUser(user1);
        List<Film> recommendations = filmStorage.getRecommendations(user1Added.getId());
        assertThat(recommendations, hasSize(0));
    }

    @Test
    void getRecommendationsIfNoTheSameLikes() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User user1Added = userStorage.addUser(user1);
        User user2Added = userStorage.addUser(user2);
        Film film1 = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film film2 = Film.builder()
                .name("Psycho2")
                .description("Американский психологический хоррор 2")
                .releaseDate(LocalDate.of(1962, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film film3 = Film.builder()
                .name("Film3")
                .description("Description3")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film film1Added = filmStorage.addFilm(film1);
        Film film2Added = filmStorage.addFilm(film2);
        Film film3Added = filmStorage.addFilm(film3);
        List<Film> recommendations = filmStorage.getRecommendations(user1Added.getId());
        assertThat(recommendations, hasSize(0));
        likesStorage.addLike(film1Added.getId(), user1Added.getId());
        likesStorage.addLike(film2Added.getId(), user2Added.getId());
        likesStorage.addLike(film3Added.getId(), user2Added.getId());
        List<Film> recommendations2 = filmStorage.getRecommendations(user1Added.getId());
        assertThat(recommendations2, hasSize(0));
    }

    @Test
    void getRecommendationsIfTheSameLikes() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User user1Added = userStorage.addUser(user1);
        User user2Added = userStorage.addUser(user2);

        Film film1 = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film film2 = Film.builder()
                .name("Psycho2")
                .description("Американский психологический хоррор 2")
                .releaseDate(LocalDate.of(1962, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film film3 = Film.builder()
                .name("Film3")
                .description("Description3")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film film1Added = filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        List<Film> recommendations = filmStorage.getRecommendations(user1Added.getId());
        assertThat(recommendations, hasSize(0));
        likesStorage.addLike(film1Added.getId(), user1Added.getId());
        likesStorage.addLike(film1Added.getId(), user2Added.getId());
        List<Film> recommendations2 = filmStorage.getRecommendations(user1Added.getId());
        assertThat(recommendations2, hasSize(0));
    }

    @Test
    void getRecommendations() {
        User user1 = User.builder()
                .email("user1@yandex.ru")
                .login("user1")
                .name("User1")
                .birthday(LocalDate.of(1991, 1, 1))
                .build();
        User user2 = User.builder()
                .email("user2@yandex.ru")
                .login("user2")
                .name("User2")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User user1Added = userStorage.addUser(user1);
        User user2Added = userStorage.addUser(user2);
        Film film1 = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film film2 = Film.builder()
                .name("Psycho2")
                .description("Американский психологический хоррор 2")
                .releaseDate(LocalDate.of(1962, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        Film film3 = Film.builder()
                .name("Film3")
                .description("Description3")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(List.of(Genre.builder().id(2).name("Драма").build()))
                .build();
        Film film1Added = filmStorage.addFilm(film1);
        Film film2Added = filmStorage.addFilm(film2);
        Film film3Added = filmStorage.addFilm(film3);
        List<Film> recommendations = filmStorage.getRecommendations(user1Added.getId());
        assertThat(recommendations, hasSize(0));
        likesStorage.addLike(film1Added.getId(), user1Added.getId());
        likesStorage.addLike(film1Added.getId(), user2Added.getId());
        likesStorage.addLike(film2Added.getId(), user2Added.getId());
        likesStorage.addLike(film3Added.getId(), user1Added.getId());
        List<Film> recommendations2 = filmStorage.getRecommendations(user1Added.getId());
        assertThat(recommendations2, hasSize(1));
        assertThat(recommendations2.get(0).getId(), equalTo(2L));
        List<Film> recommendations3 = filmStorage.getRecommendations(user2Added.getId());
        assertThat(recommendations3, hasSize(1));
        assertThat(recommendations3.get(0).getId(), equalTo(3L));
    }

    @Test
    void addDirectorTest() {
        Director director = Director.builder()
                .name("Стивен Спилберг")
                .build();
        assertThat(1L, equalTo(directorStorage.addDirector(director).getId()));
    }

    @Test
    void updateDirectorTest() {
        Director director = Director.builder()
                .name("Стивен Спилберг")
                .build();
        director = directorStorage.addDirector(director);
        director.setName("Гайдай");
        assertThat("Гайдай", equalTo(directorStorage.updateDirector(director).getName()));
    }

    @Test
    void getDirectorsTest() {
        Director director = Director.builder()
                .name("Стиве Спилберг")
                .build();
        directorStorage.addDirector(director);
        assertThat(1, equalTo(directorStorage.getDirectors().size()));
    }

    @Test
    void getDirectorByIdTest() {
        Director director = Director.builder()
                .name("Стивен Спилберг")
                .build();
        directorStorage.addDirector(director);
        assertThat(1L, equalTo(directorStorage.getDirectorById(1).getId()));
    }

    @Test
    void removeDirectorByIdTest() {
        Director director = Director.builder()
                .name("Стивен Спилберг")
                .build();
        directorStorage.addDirector(director);
        assertThat(true, equalTo(directorStorage.removeDirectorById(1)));
    }

    @Test
    void getListOfFilmDirectors() {
        Director director = Director.builder()
                .name("Альфред Хичкок")
                .build();
        director = directorStorage.addDirector(director);
        ArrayList<Director> directors = new ArrayList<>();
        directors.add(director);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .directors(directors)
                .build();
        filmStorage.addFilm(film);
        assertThat(1, equalTo(directorStorage.getListOfFilmDirectors(1).size()));
    }

    @Test
    void getListOfDirectorFilmsTest() {
        Director director = Director.builder()
                .name("Альфред Хичкок")
                .build();
        director = directorStorage.addDirector(director);
        ArrayList<Director> directors = new ArrayList<>();
        directors.add(director);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года, снятый режиссёром Альфредом Хичкоком.")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .directors(directors)
                .build();
        filmStorage.addFilm(film);
        assertThat(1, equalTo(filmStorage.getListOfDirectorFilms(1).size()));
    }

    @Test
    void getFilmsByTitleKeywordTest() {
        addFilmsAndDirectorsForSearch("upd");
        assertThat(1, equalTo(filmStorage.getFilmsByTitleKeyword("upd").size()));
        assertThat(0, equalTo(filmStorage.getFilmsByTitleKeyword("npd").size()));
    }

    @Test
    void getFilmsByDirectorKeywordTest() {
        addFilmsAndDirectorsForSearch("upd");
        assertThat(1, equalTo(filmStorage.getFilmsByDirectorKeyword("upd").size()));
        assertThat(0, equalTo(filmStorage.getFilmsByDirectorKeyword("npd").size()));
    }

    @Test
    void getFilmsByTitleAndDirectorKeywordTest() {
        addFilmsAndDirectorsForSearch("upd");
        assertThat(2, equalTo(filmStorage.getFilmsByTitleAndDirectorKeyword("upd").size()));
        assertThat(0, equalTo(filmStorage.getFilmsByTitleAndDirectorKeyword("not found").size()));
    }

    void addFilmsAndDirectorsForSearch(String query) {
        Director director = Director.builder()
                .name("Альфред Хичкок " + query)
                .build();
        director = directorStorage.addDirector(director);
        ArrayList<Director> directors = new ArrayList<>();
        directors.add(director);
        Film film = Film.builder()
                .name("Psycho1")
                .description("Американский психологический хоррор 1960 года")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .directors(directors)
                .build();
        filmStorage.addFilm(film);

        director = Director.builder()
                .name("Альфред Хичкок")
                .build();
        director = directorStorage.addDirector(director);
        ArrayList<Director> directors1 = new ArrayList<>();
        directors1.add(director);
        film = Film.builder()
                .name("Psycho1 " + query)
                .description("Американский психологический хоррор 1960 года")
                .releaseDate(LocalDate.of(1960, 1, 1))
                .duration(109)
                .rate(1)
                .mpa(Mpa.builder().id(1).name("G").build())
                .likes(new ArrayList<>())
                .genres(new ArrayList<>())
                .directors(directors1)
                .build();
        filmStorage.addFilm(film);
    }

}
