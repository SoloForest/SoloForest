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

    if (!emailRegExp.test(form.email.value)) {
        toastWarning('이메일 형식을 지켜야 합니다.');
        form.email.focus();
        return false;
    }

    form.submit();
}

function ModifyForm__submit(form) {
    form.password.value = form.password.value.trim();
    form.passwordCheck.value = form.passwordCheck.value.trim();
    form.nickname.value = form.nickname.value.trim();
    form.email.value = form.email.value.trim();

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

    if (!emailRegExp.test(form.email.value)) {
        toastWarning('이메일 형식을 지켜야 합니다.');
        form.email.focus();
        return false;
    }

    form.submit();
}

function Withdraw__Submit(form) {
    let isConfirm = confirm('탈퇴한 회원은 복구할 수 없습니다.\n그래도 탈퇴하시겠습니까?');
    if (isConfirm === true) {
        let input = prompt('비밀번호를 입력해주세요.');
        if (input != null && passwordValueMinLength <= input.trim().length && input.trim().length <= passwordValueMaxLength) {
            let header = $("meta[name='_csrf_header']").attr('content');
            let token = $("meta[name='_csrf']").attr('content');

            $.ajax({
                url: form.action,
                type: "POST",
                data: {
                    _csrf: form._csrf.value,
                    password: input
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                },
                success: function (result) {
                    alert('탈퇴 완료');
                    window.location.href = '/main';
                },
                error: function (request, status, error) {
                    alert('올바르지 않은 비밀번호입니다.');
                }
            })
        } else if (passwordValueMinLength > input.trim().length || passwordValueMaxLength < input.trim().length)
            alert('올바르지 않은 비밀번호입니다.');
        else {
            alert('취소되었습니다.');
        }
    }
}