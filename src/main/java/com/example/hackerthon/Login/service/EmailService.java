package com.example.hackerthon.Login.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendResetCode(String to, String code) throws MessagingException {
        String subject = "비밀번호 재설정 인증번호";
        String content = "요청하신 비밀번호 재설정을 위한 인증번호는 다음과 같습니다:\n\n"
                + code + "\n\n10분 이내에 입력해주세요.";
        sendEmail(to, subject, content);
    }

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true); // html=true
        helper.setFrom("op9563_@naver.com");
        mailSender.send(message);
    }
}
