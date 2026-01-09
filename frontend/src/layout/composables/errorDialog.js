import { ref } from 'vue';

const errorDialogVisible = ref(false);
const errorDialogMessage = ref('');
const errorDialogDetails = ref([]);
const errorDialogType = ref('info'); // nuevo campo

export function useErrorDialog() {
  
  const showErrorDialog = (backendError, type = 'error') => {

    if (!backendError) return;

    errorDialogMessage.value = backendError.message || 'Erro inesperado';

    if (typeof backendError.detail === 'string') {
      errorDialogDetails.value = backendError.detail.split(',');
    } else if (Array.isArray(backendError.detail)) {
      errorDialogDetails.value = backendError.detail;
    } else {
      errorDialogDetails.value = backendError.detail ? [backendError.detail] : [];
    }

    errorDialogType.value = type;
    errorDialogVisible.value = true;
  };

  const closeErrorDialog = () => {
    errorDialogVisible.value = false;
    errorDialogMessage.value = '';
    errorDialogDetails.value = [];
    errorDialogType.value = 'info';
  };

  return {
    errorDialogVisible,
    errorDialogMessage,
    errorDialogDetails,
    errorDialogType,
    showErrorDialog,
    closeErrorDialog
  };
}
