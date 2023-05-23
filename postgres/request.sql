with p4 as (with p3 as (with p2 as (with p1 as (select p.invoice_id,
                                                       p.payment_id,
                                                       p.currency_code,
                                                       p.event_created_at,
                                                       s.status
                                                from dw.payment as p
                                                         inner join dw.payment_status_info as s
                                                                    on p.invoice_id = s.invoice_id and
                                                                       p.payment_id = s.payment_id and s.current
--                                                 where p.event_created_at > now() - interval '?1 second'
                                                order by p.event_created_at desc)
                                    select p1.*,
                                           c.issuer_country as country
                                    from p1
                                             left join dw.payment_payer_info as c
                                                       on p1.invoice_id = c.invoice_id and p1.payment_id = c.payment_id)
                        select p2.*,
                               pt.route_provider_id as provider_id,
                               pt.route_terminal_id as terminal_id
                        from p2
                                 inner join dw.payment_route as pt
                                            on p2.invoice_id = pt.invoice_id and p2.payment_id = pt.payment_id and
                                               pt.current)
            select p3.*,
                   p.name as provider_name
            from p3
                     inner join dw.provider as p
                                on p3.provider_id = p.provider_ref_id and p.current)
select p4.*,
       t.name as terminal_name
from p4
         inner join dw.terminal as t
                    on p4.terminal_id = t.terminal_ref_id and t.current;
