const usernameValueMinLength = 4;
const usernameValueMaxLength = 16;
const passwordValueMinLength = 4;
const passwordValueMaxLength = 16;
const nicknameValueMinLength = 2;
const nicknameValueMaxLength = 32;
const emailRegExp = new RegExp(/^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i);

function LoginForm__submit(form) {
    form.username.value = form.username.value.trim();
    form.password.value = form.password.value.trim();

    if (form.username.value.length < usernameValueMinLength || usernameValueMaxLength < form.username.value.length) {
        toastWarning('username은 공백 없이 4자 이상 16자 이하로 작성해야 합니다.');
        form.username.focus();
        return false;
    }

    if (form.password.value.length < passwordValueMinLength || passwordValueMaxLength < form.password.value.length) {
        toastWarning('password는 공백 없이 4자 이상 16자 이하로 작성해야 합니다.');
        form.password.focus();
        return false;
    }

    form.submit();
}

function SignUpForm__submit(form) {
    form.username.value = form.username.value.trim();
    form.password.value = form.password.value.trim();
    form.passwordCheck.value = form.passwordCheck.value.trim();
    form.nickname.value = form.nickname.value.trim();
    form.email.value = form.email.value.trim();

    if (form.username.value.length < usernameValueMinLength || usernameValueMaxLength < form.username.value.length) {
        toastWarning('username은 공백 없이 4자 이상 16자 이하로 작성해야 합니다.');
        form.username.focus();
        return false;
    }

    if (form.password.value.length < passwordValueMinLength || passwordValueMaxLength < form.password.value.length) {
        toastWarning('password는 공백 없이 4자 이상 16자 이하로 작성해야 합니다.');
        form.password.focus();
        return false;
    }

    if (form.password.value !== form.passwordCheck.value) {
        toastWarning('동일한 password를 입력해주세요');
        form.password.focus();
        return false;
    }

    if (form.nickname.value.length < nicknameValueMinLength || nicknameValueMaxLength < form.username.value.length) {
        toastWarning('nickname은 공백 없이 2자 이상 32자 이하로 작성해야 합니다.');
        form.nickname.focus();
        return false;
    }

    if (form.nickname.value.length < nicknameValueMinLength || nicknameValueMaxLength < form.username.value.length) {
        toastWarning('nickname은 공백 없이 2자 이상 32자 이하로 작성해야 합니다.');
        form.nickname.focus();
        return false;
    }

    if (!emailRegExp.test(form.email.value)) {
        toastWarning('이메일 형식을 지켜야 합니다.');
        form.email.focus();
        return false;
    }

    form.submit();
}