import { useQuery } from '@tanstack/react-query'
import { api } from './api'
import type { BusinessProfile } from './types'

export function useBusinessProfile() {
  return useQuery({
    queryKey: ['business-profile'],
    queryFn: () => api.get<BusinessProfile>('/api/settings/business-profile'),
    staleTime: 5 * 60 * 1000,
  })
}
