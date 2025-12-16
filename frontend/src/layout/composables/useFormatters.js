export function useFormatters(locale = 'pt-PT') {

    const formatDateTime = (dateString) => {
        if (!dateString) return 'N/A';

        const date = new Date(dateString);
        if (isNaN(date)) return dateString;

        return date.toLocaleDateString(locale, {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        if (isNaN(date)) return dateString;

        return date.toLocaleDateString(locale);
    };

    const formatNumber = (value) => {
        if (value === null || value === undefined) return '—';
        return new Intl.NumberFormat(locale).format(value);
    };

    const formatCurrency = (value, currency = 'EUR') => {
        if (value === null || value === undefined) return '—';
        return new Intl.NumberFormat(locale, {
            style: 'currency',
            currency
        }).format(value);
    };

    return {
        formatDateTime,
        formatDate,
        formatNumber,
        formatCurrency
    };
}
