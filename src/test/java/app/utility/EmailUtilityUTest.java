package app.utility;

import app.notification.client.dto.Email;
import app.utils.EmailUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EmailUtilityUTest {

    @Test
    void getFailedEmailsCount_whenPassListOf2SucceededAnd1FailedEmails_thenReturn1() {

        Email oneSucceeded = Email.builder().status("SUCCEEDED").build();
        Email twoSucceeded = Email.builder().status("SUCCEEDED").build();
        Email threeFailed = Email.builder().status("FAILED").build();

        List<Email> emails = List.of(oneSucceeded, twoSucceeded, threeFailed);

        long result = EmailUtils.getFailedEmailsCount(emails);

        assertEquals(1, result);
    }

    @Test
    void getFailedEmailsCount_whenPassEmptyList_thenReturn0() {

        long result = EmailUtils.getFailedEmailsCount(List.of());

        assertEquals(0, result);
    }
}