	.syntax unified
	.arm

	.global f42_player_timestep_entry
	.type f42_player_timestep_entry, %function
f42_player_timestep_entry:
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_update_pos
	stmdb	sp!, {r4-r8, lr}
	mov	r4, r0
	ldr	r5, [r4, #0x9c]
	mov	r12, r5, asr #1
	str	r12, [r4, #0x9c]
	ldr	r12, .L_horz_ang
	blx	r12
	add	r0, r4, #0xa4
	ldmia	r0, {r6-r8}
	mov	r6, r6, asr #1
	mov	r7, r7, asr #1
	add	r7, r7, r5, asr #3
	mov	r8, r8, asr #1
	stmia	r0, {r6-r8}
	mov	r0, r4
	add	r1, r4, #0x2d4
	ldr	r12, .L_only_speed
	blx	r12
	sub	r7, r7, r5, asr #3
	mov	r7, r7, lsl #1
	str	r7, [r4, #0xa8]
	str	r5, [r4, #0x9c]
	ldmia	sp!, {r4-r8, pc}

.L_update_pos:
	.word	0xEAFE6BE8

	.global f42_player_speed_entry
	.type f42_player_speed_entry, %function
f42_player_speed_entry:
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_speed_original
	cmp	r2, #0
	movgt	r2, r2, asr #1
.L_speed_original:
	.word	0xE92D4030
	.word	0xEA017C37

	.align	2
.L_cadence:
	.word	0x0208EE44
.L_horz_ang:
	.word	0x02010C5C
.L_only_speed:
	.word	0x02010D40

	.global f42_player_timer_entry
	.type f42_player_timer_entry, %function
f42_player_timer_entry:
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_timer_run
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	beq	.L_timer_skip
.L_timer_run:
	add	r0, r5, r0
	.word	0xEA01BC8C
.L_timer_skip:
	.word	0xEA01BCB4

	.global f42_player_control_timer_entry
	.type f42_player_control_timer_entry, %function
f42_player_control_timer_entry:
	ldr	r12, .L_cadence
	ldr	r12, [r12]
	cmp	r12, #1
	bne	.L_control_timer_run
	ldr	r12, .L_game_loop_counter
	ldr	r12, [r12]
	tst	r12, #1
	beq	.L_control_timer_skip
.L_control_timer_run:
	add	r0, r5, #0x6c0
	.word	0xEA01BCB4
.L_control_timer_skip:
	.word	0xEA01BCDE

.L_game_loop_counter:
	.word	0x020A0DB0
	.size f42_player_timestep_entry, .-f42_player_timestep_entry
