import { defineStore } from 'pinia'
import { ref } from 'vue'
export const useUserStore = defineStore('user', () => {
    const token = ref('')
    const username = ref('')
    const user = ref(null)

    const setToken = (newToken) => {
        token.value = newToken
    }
    const setUsername = (newUsername) => {
        username.value = newUsername
    }
    const setUser = (newUser) => {
        user.value = newUser
    }

    const removeToken = () => {
        token.value = ''
    }
    const removeUsername = () => {
        username.value = ''
    }
    const removeUser = () => {
        user.value = ''
    }

    return {
        token,
        username,
        user,

        setToken,
        setUsername,
        setUser,
        removeUsername,
        removeToken,
        setUser
        
    }
}, {
    persist: true
})