with w4 as (with w3 as (with w2 as (with w1 as (select w.wallet_id,
                                                       coalesce(w.provider_id, -1)                    as provider_id,
                                                       cast(coalesce(w.terminal_id, '-1') as integer) as terminal_id,
                                                       w.currency_code,
                                                       w.withdrawal_status,
                                                       w.amount
                                                from dw.withdrawal as w
                                                where w.event_created_at > now() - interval '60 second'
                                                  and w.current)
                                    select w1.*,
                                           p.name as provider_name
                                    from w1
                                             inner join dw.provider as p
                                                        on w1.provider_id = p.provider_ref_id and
                                                           p.current)
                        select w2.*,
                               t.name as terminal_name
                        from w2
                                 inner join dw.terminal as t
                                            on w2.terminal_id = t.terminal_ref_id and
                                               t.current)
            select w3.*,
                   w.wallet_name as wallet_name
            from w3
                     inner join dw.wallet as w
                                on w3.wallet_id = w.wallet_id and
                                   w.current)
select provider_id,
       provider_name,
       terminal_id,
       terminal_name,
       wallet_id,
       wallet_name,
       currency_code,
       withdrawal_status,
       count(withdrawal_status),
       sum(amount)
from w4
group by provider_id,
         provider_name,
         terminal_id,
         terminal_name,
         wallet_id,
         wallet_name,
         currency_code,
         withdrawal_status
