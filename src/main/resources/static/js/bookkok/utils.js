// phone을 DB 저장용으로 통일 (010XXXXXXXX)
function normalizePhone(phone) {
    if (!phone) return '';

    // + 제거 + 숫자만
    phone = phone.replace(/\D/g, '');

    // 국제번호 제거 (E.164 기준 1~3자리 국가코드)
    // 한국 +82 / 미국 +1 등 → 완전 일반화
    if (phone.length > 10 && phone.startsWith('8')) {
        phone = phone.slice(2);
    } else if (phone.length > 10 && phone.startsWith('1')) {
        phone = phone.slice(1);
    }

    // 한국 번호 보정
    if (!phone.startsWith('0')) {
        phone = '0' + phone;
    }

    return phone;
}

// 화면 표시용 (010-1234-5678)
function formatPhoneNumber(phone) {
    if (!phone) return '';

    phone = phone.replace(/\D/g, '');

    if (phone.length === 11) {
        return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
    }

    return phone;
}