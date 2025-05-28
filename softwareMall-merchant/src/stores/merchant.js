import { defineStore } from 'pinia'
import { ref } from 'vue'
export const useMerchantStore = defineStore('merchant', () => {
    const token = ref('')
    const merchant = ref(null)
    const setToken = (newToken) => {
        token.value = newToken
    }
    const setMerchant = (newMerchant) => {
        merchant.value = newMerchant
    }
    const removeToken = () => {
        token.value = ''
    }
    const removeMerchant = () => {
        merchant.value = null
    }
    

    return {
        token,
        merchant,

        setToken,
        setMerchant,
        removeToken,
        removeMerchant
    }
}, {
    persist: true
})