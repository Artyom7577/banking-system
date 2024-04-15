package am.greenbank.helpers.mappers;

import am.greenbank.dtos.UserDto;
import am.greenbank.entities.account.Account;
import am.greenbank.entities.cards.Card;
import am.greenbank.entities.user.User;
import am.greenbank.entities.user.UserRole;
import am.greenbank.repositories.interfaces.AccountRepository;
import am.greenbank.repositories.interfaces.CardRepository;
import am.greenbank.requests.RegisterRequest;
import am.greenbank.requests.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public final class UserMapper {
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final DateTimeFormatter dateFormatter;

    public UserMapper(
        AccountRepository accountRepository,
        CardRepository cardRepository,
        @Qualifier(value = "dateFormatter")
        DateTimeFormatter dateFormatter
    ) {
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.dateFormatter = dateFormatter;
    }

    public UserDto mapUserToUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .birthday(user.getBirthday().format(dateFormatter))
            .phone(user.getPhone())
            .img(user.getImg())
            .role(user.getRole())
            .accounts(
                user.getAccounts()
                    .stream()
                    .map(Account::getId)
                    .toList()
            )
            .cards(
                user.getCards()
                    .stream()
                    .map(Card::getId)
                    .toList()
            )
            .build();
    }

    public User mapUserDtoToUser(UserDto userDto) {
        return User.builder()
            .id(userDto.getId())
            .firstName(userDto.getFirstName())
            .lastName(userDto.getLastName())
            .birthday(LocalDate.parse(userDto.getBirthday(), dateFormatter))
            .img(userDto.getImg())
            .role(userDto.getRole())
            .accounts(
                userDto.getAccounts()
                    .stream()
                    .map(
                        accountId -> accountRepository.findAccountById(accountId).orElse(null)
                    ).toList()
            )
            .cards(
                userDto.getCards()
                    .stream()
                    .map(
                        cardId -> cardRepository.findCardById(cardId).orElse(null)
                    )
                    .toList()
            )
            .locked(false)
            .enabled(false)
            .build();
    }

    public User mapRegisterUserDtoToUser(RegisterRequest userDto) {
        return User.builder()
            .firstName(userDto.getFirstName())
            .lastName(userDto.getLastName())
            .email(userDto.getEmail())
            .birthday(LocalDate.parse(userDto.getBirthday(), dateFormatter))
            .password(userDto.getPassword())
            .phone(userDto.getPhone())
            .role(UserRole.USER)
            .locked(false)
            .enabled(false)
            .build();
    }

    public User mapUpdateUserRequestToUser(UpdateUserRequest request) {
        return User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phone(request.getPhone())
            .build();
    }
}
