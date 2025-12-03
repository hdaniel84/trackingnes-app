// src/composables/notify.js
import { useToast } from 'primevue/usetoast';

export function useNotify() {
  const toast = useToast();

  const notifySuccess = (detail, summary = 'Sucesso') => {
    toast.add({ severity: 'success', summary, detail, life: 3000 });
  };

  const notifyError = (detail, summary = 'Erro') => {
    toast.add({ severity: 'error', summary, detail, life: 5000 });
  };

  const notifyInfo = (detail, summary = 'Info') => {
    toast.add({ severity: 'info', summary, detail, life: 4000 });
  };

  const notifyWarn = (detail, summary = 'Aviso') => {
    toast.add({ severity: 'warn', summary, detail, life: 4000 });
  };

  return { notifySuccess, notifyError, notifyInfo, notifyWarn };
}
