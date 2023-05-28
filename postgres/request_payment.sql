with p6 as (with p5 as (with p4 as (with p3 as (with p2 as (with p1 as (select p.invoice_id,
                                                                               p.payment_id,
                                                                               p.party_id,
                                                                               p.shop_id,
                                                                               p.currency_code,
                                                                               psi.status
                                                                        from dw.payment as p
                                                                                 inner join dw.payment_status_info as psi
                                                                                            on p.invoice_id =
                                                                                               psi.invoice_id and
                                                                                               p.payment_id =
                                                                                               psi.payment_id and
                                                                                               psi.current
                                                                        where p.event_created_at > now() - interval '30 second')
                                                            select p1.*,
                                                                   coalesce(ppi.issuer_country, 'undefined')           as issuer_country,
                                                                   coalesce(ppi.bank_name, 'undefined')                as issuer_bank,
                                                                   coalesce(ppi.bank_card_payment_system, 'undefined') as issuer_bank_card_payment_system
                                                            from p1
                                                                     left join dw.payment_payer_info as ppi
                                                                               on p1.invoice_id = ppi.invoice_id and
                                                                                  p1.payment_id = ppi.payment_id)
                                                select p2.*,
                                                       coalesce(pr.route_provider_id, -1) as provider_id,
                                                       coalesce(pr.route_terminal_id, -1) as terminal_id
                                                from p2
                                                         inner join dw.payment_route as pr
                                                                    on p2.invoice_id = pr.invoice_id and
                                                                       p2.payment_id = pr.payment_id and
                                                                       pr.route_provider_id not in (1) and
                                                                       pr.current)
                                    select p3.*,
                                           p.name as provider_name
                                    from p3
                                             inner join dw.provider as p
                                                        on p3.provider_id = p.provider_ref_id and
                                                           p.current)
                        select p4.*,
                               t.name as terminal_name
                        from p4
                                 inner join dw.terminal as t
                                            on p4.terminal_id = t.terminal_ref_id and
                                               t.current)
            select p5.*,
                   s.details_name as shop_name
            from p5
                     inner join dw.shop as s
                                on p5.shop_id = s.shop_id and
                                   s.current)
select provider_id,
       provider_name,
       terminal_id,
       terminal_name,
       shop_id,
       shop_name,
       currency_code,
       issuer_country,
       issuer_bank,
       issuer_bank_card_payment_system,
       status,
       count(status)
from p6
group by provider_id,
         provider_name,
         terminal_id,
         terminal_name,
         shop_id,
         shop_name,
         currency_code,
         issuer_country,
         issuer_bank,
         issuer_bank_card_payment_system,
         status
