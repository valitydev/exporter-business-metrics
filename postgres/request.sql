with p2 as
         (with p1 as
                   (select p.invoice_id,
                           p.payment_id,
                           s.status,
                           p.event_created_at,
                           c.issuer_country as country,
                           p.currency_code
                    from dw.payment as p
                             inner join dw.payment_status_info as s
                                        on p.invoice_id = s.invoice_id and p.payment_id = s.payment_id and s.current
                             left join dw.payment_payer_info as c
                                       on p.invoice_id = c.invoice_id and p.payment_id = c.payment_id
--                     where p.event_created_at > now() - interval '?1 second'
                    order by p.event_created_at desc)
          select p1.invoice_id,
                 p1.payment_id,
                 p1.status,
                 p1.event_created_at,
                 p1.country,
                 p1.currency_code,
                 pt.route_provider_id as provider_id,
                 pt.route_terminal_id as terminal_id
          from p1
                   inner join dw.payment_route as pt
                              on p1.invoice_id = pt.invoice_id and p1.payment_id = pt.payment_id and pt.current)
select p2.invoice_id,
       p2.payment_id,
       p2.status,
       p2.event_created_at,
       p2.country,
       p2.currency_code,
       p2.provider_id,
       p2.terminal_id,
       p.name as provider_name,
       t.name as terminal_name
from p2
         inner join dw.provider as p
                    on p2.provider_id = p.provider_ref_id and p.current
         inner join dw.terminal as t
                    on p2.terminal_id = t.terminal_ref_id and t.current
