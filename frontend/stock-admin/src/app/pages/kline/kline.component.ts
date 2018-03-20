import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-basic',
  templateUrl: './kline.component.html',
  styleUrls: ['./kline.component.scss']
})
export class KlineComponent implements OnInit {
  demo_html = `<div echarts [options]="options" class="demo-chart"></div>`;
  demo_ts = `\
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-basic',
  templateUrl: './kline.component.html',
  styleUrls: ['./kline.component.scss']
})
export class KlineComponent implements OnInit {
  options: any;

  constructor() { }

  ngOnInit() {
    let xAxisData = [];
    let data1 = [];
    let data2 = [];

    for (let i = 0; i < 100; i++) {
      xAxisData.push('category' + i);
      data1.push((Math.sin(i / 5) * (i / 5 - 10) + i / 6) * 5);
      data2.push((Math.cos(i / 5) * (i / 5 - 10) + i / 6) * 5);
    }

    this.options = {
      legend: {
        data: ['bar', 'bar2'],
        align: 'left'
      },
      tooltip: {},
      xAxis: {
        data: xAxisData,
        silent: false,
        splitLine: {
          show: false
        }
      },
      yAxis: {
      },
      series: [{
        name: 'bar',
        type: 'bar',
        data: data1,
        animationDelay: function (idx) {
          return idx * 10;
        }
      }, {
        name: 'bar2',
        type: 'bar',
        data: data2,
        animationDelay: function (idx) {
          return idx * 10 + 100;
        }
      }],
      animationEasing: 'elasticOut',
      animationDelayUpdate: function (idx) {
        return idx * 5;
      }
    };
  }
}`;

  options: any;

  constructor() {
  }

  rawData: string[][] = [['2015/12/31', '3570.47', '3539.18', '-33.69', '-0.94%', '3538.35', '3580.6', '176963664', '25403106', '-'],
    ['2015/12/30', '3566.73', '3572.88', '9.14', '0.26%', '3538.11', '3573.68', '187889600', '26778766', '-'],
    ['2015/12/29', '3528.4', '3563.74', '29.96', '0.85%', '3515.52', '3564.17', '182551920', '25093890', '-'],
    ['2015/12/28', '3635.77', '3533.78', '-94.13', '-2.59%', '3533.78', '3641.59', '269983264', '36904280', '-'],
    ['2015/12/25', '3614.05', '3627.91', '15.43', '0.43%', '3601.74', '3635.26', '198451120', '27466004', '-'],
    ['2015/12/24', '3631.31', '3612.49', '-23.6', '-0.65%', '3572.28', '3640.22', '227785216', '31542126', '-'],
    ['2015/12/23', '3653.28', '3636.09', '-15.68', '-0.43%', '3633.03', '3684.57', '298201792', '41990292', '-'],
    ['2015/12/22', '3645.99', '3651.77', '9.3', '0.26%', '3616.87', '3652.63', '261178752', '36084604', '-'],
    ['2015/12/21', '3568.58', '3642.47', '63.51', '1.77%', '3565.75', '3651.06', '299849280', '39831696', '-'],
    ['2015/12/18', '3574.94', '3578.96', '-1.03', '-0.03%', '3568.16', '3614.7', '273707904', '36538580', '-'],
    ['2015/12/17', '3533.63', '3580', '63.81', '1.81%', '3533.63', '3583.41', '283856480', '38143960', '-'],
    ['2015/12/16', '3522.09', '3516.19', '5.83', '0.17%', '3506.29', '3538.69', '193482304', '26528864', '-'],
    ['2015/12/15', '3518.13', '3510.35', '-10.31', '-0.29%', '3496.85', '3529.96', '200471344', '27627494', '-'],
    ['2015/12/14', '3403.51', '3520.67', '86.09', '2.51%', '3399.28', '3521.78', '215374624', '27921354', '-'],
    ['2015/12/11', '3441.6', '3434.58', '-20.91', '-0.61%', '3410.92', '3455.55', '182908880', '24507642', '-'],
    ['2015/12/10', '3469.81', '3455.5', '-16.94', '-0.49%', '3446.27', '3503.65', '200427520', '27949970', '-'],
    ['2015/12/9', '3462.58', '3472.44', '2.37', '0.07%', '3454.88', '3495.7', '195698848', '26785488', '-'],
    ['2015/12/8', '3518.65', '3470.07', '-66.86', '-1.89%', '3466.79', '3518.65', '224367312', '29782174', '-'],
    ['2015/12/7', '3529.81', '3536.93', '11.94', '0.34%', '3506.62', '3543.95', '208302576', '28056158', '-'],
    ['2015/12/4', '3558.15', '3524.99', '-59.83', '-1.67%', '3510.41', '3568.97', '251736416', '31976682', '-'],
    ['2015/12/3', '3525.73', '3584.82', '47.92', '1.35%', '3517.23', '3591.73', '281111232', '33885908', '-'],
    ['2015/12/2', '3450.28', '3536.91', '80.6', '2.33%', '3427.66', '3538.85', '301491488', '36918304', '-'],
    ['2015/12/1', '3442.44', '3456.31', '10.9', '0.32%', '3417.54', '3483.41', '252390752', '33025674', '-'],
    ['2015/11/30', '3433.85', '3445.4', '9.1', '0.26%', '3327.81', '3470.37', '304197888', '38750364', '-'],
    ['2015/11/27', '3616.54', '3436.3', '-199.25', '-5.48%', '3412.43', '3621.9', '354287520', '46431124', '-'],
    ['2015/11/26', '3659.57', '3635.55', '-12.38', '-0.34%', '3629.86', '3668.38', '306761600', '42624744', '-'],
    ['2015/11/25', '3614.07', '3647.93', '31.82', '0.88%', '3607.52', '3648.37', '273024864', '38080292', '-'],
    ['2015/11/24', '3602.89', '3616.11', '5.79', '0.16%', '3563.1', '3616.48', '248810512', '32775852', '-'],
    ['2015/11/23', '3630.87', '3610.31', '-20.18', '-0.56%', '3598.87', '3654.75', '315997472', '41414824', '-'],
    ['2015/11/20', '3620.79', '3630.5', '13.44', '0.37%', '3607.92', '3640.53', '310801984', '41391088', '-'],
    ['2015/11/19', '3573.78', '3617.06', '48.59', '1.36%', '3561.04', '3618.21', '247915584', '32844260', '-'],
    ['2015/11/18', '3605.06', '3568.47', '-36.33', '-1.01%', '3558.7', '3617.07', '297580736', '39233876', '-'],
    ['2015/11/17', '3629.98', '3604.8', '-2.16', '-0.06%', '3598.07', '3678.27', '383575456', '52152036', '-'],
    ['2015/11/16', '3522.46', '3606.96', '26.12', '0.73%', '3519.42', '3607.61', '276187040', '36942184', '-'],
    ['2015/11/13', '3600.76', '3580.84', '-52.06', '-1.43%', '3564.81', '3632.56', '345870944', '46866864', '-'],
    ['2015/11/12', '3656.82', '3632.9', '-17.35', '-0.48%', '3603.23', '3659.31', '361717600', '48283260', '-'],
    ['2015/11/11', '3635', '3650.25', '9.76', '0.27%', '3605.62', '3654.88', '360972672', '46782220', '-'],
    ['2015/11/10', '3617.4', '3640.49', '-6.4', '-0.18%', '3607.89', '3669.53', '429746592', '56005512', '-'],
    ['2015/11/9', '3588.5', '3646.88', '56.85', '1.58%', '3588.5', '3673.76', '503016704', '63618404', '-'],
    ['2015/11/6', '3514.44', '3590.03', '67.21', '1.91%', '3508.83', '3596.38', '429167040', '54328220', '-'],
    ['2015/11/5', '3459.22', '3522.82', '63.18', '1.83%', '3455.53', '3585.66', '553254976', '67867464', '-'],
    ['2015/11/4', '3325.62', '3459.64', '142.94', '4.31%', '3325.62', '3459.65', '339078752', '42610440', '-'],
    ['2015/11/3', '3330.32', '3316.7', '-8.39', '-0.25%', '3302.18', '3346.27', '192897440', '24436056', '-'],
    ['2015/11/2', '3337.58', '3325.08', '-57.48', '-1.70%', '3322.31', '3391.06', '230951136', '28601932', '-'],
    ['2015/10/30', '3380.28', '3382.56', '-4.75', '-0.14%', '3346.59', '3417.2', '243595120', '30726678', '-'],
    ['2015/10/29', '3387.77', '3387.32', '12.12', '0.36%', '3362.51', '3411.71', '235676016', '29450842', '-'],
    ['2015/10/28', '3417.01', '3375.2', '-59.14', '-1.72%', '3367.23', '3439.76', '293523296', '36165620', '-'],
    ['2015/10/27', '3409.14', '3434.34', '4.76', '0.14%', '3332.62', '3441.57', '328172768', '40888724', '-'],
    ['2015/10/26', '3448.65', '3429.58', '17.15', '0.50%', '3402', '3457.52', '365560864', '45394252', '-'],
    ['2015/10/23', '3377.55', '3412.43', '43.7', '1.30%', '3360.22', '3422.02', '347372864', '42526308', '-'],
    ['2015/10/22', '3292.29', '3368.74', '48.06', '1.45%', '3282.99', '3373.78', '323739328', '37545200', '-'],
    ['2015/10/21', '3428.56', '3320.68', '-104.65', '-3.06%', '3265.44', '3447.26', '458455424', '51850924', '-'],
    ['2015/10/20', '3377.55', '3425.33', '38.63', '1.14%', '3357.86', '3425.52', '318973760', '38358252', '-'],
    ['2015/10/19', '3401.63', '3386.7', '-4.65', '-0.14%', '3355.57', '3423.4', '378112160', '45330364', '-'],
    ['2015/10/16', '3358.3', '3391.35', '53.28', '1.60%', '3334.85', '3393.02', '395460576', '45944784', '-'],
    ['2015/10/15', '3255.03', '3338.07', '75.63', '2.32%', '3254.39', '3338.3', '316283840', '36256556', '-'],
    ['2015/10/14', '3280.02', '3262.44', '-30.79', '-0.93%', '3256.25', '3307.32', '295077760', '33027752', '-'],
    ['2015/10/13', '3262.16', '3293.23', '5.57', '0.17%', '3253.25', '3298.63', '297153120', '33480608', '-'],
    ['2015/10/12', '3193.54', '3287.66', '104.51', '3.28%', '3188.41', '3318.71', '386294688', '43554100', '-'],
    ['2015/10/9', '3146.64', '3183.15', '39.79', '1.27%', '3137.79', '3192.72', '234851456', '25637910', '-'],
    ['2015/10/8', '3156.07', '3143.36', '90.58', '2.97%', '3133.13', '3172.28', '234276048', '25883034', '-'],
    ['2015/9/30', '3052.84', '3052.78', '14.64', '0.48%', '3039.74', '3073.3', '146642448', '15656919', '-'],
    ['2015/9/29', '3055.22', '3038.14', '-62.62', '-2.02%', '3021.16', '3068.3', '163222672', '16968660', '-'],
    ['2015/9/28', '3085.57', '3100.76', '8.41', '0.27%', '3042.31', '3103.07', '156727536', '16642240', '-'],
    ['2015/9/25', '3130.85', '3092.35', '-50.34', '-1.60%', '3063', '3149.95', '236263872', '24897112', '-'],
    ['2015/9/24', '3126.49', '3142.69', '26.8', '0.86%', '3109.69', '3151.16', '212887712', '23136904', '-'],
    ['2015/9/23', '3137.72', '3115.89', '-69.73', '-2.19%', '3104.74', '3164.04', '236322672', '25756004', '-'],
    ['2015/9/22', '3161.32', '3185.62', '29.08', '0.92%', '3152.48', '3213.48', '274786176', '30507132', '-'],
    ['', '', '', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', '', '', '']].reverse();


  calculateMA(dayCount: number, data: any) {
    let result = [];
    for (let i = 0, len = data.length; i < len; i++) {
      if (i < dayCount) {
        result.push('-');
        continue;
      }
      let sum = 0;
      for (let j = 0; j < dayCount; j++) {
        sum += data[i - j][1];
      }
      result.push(sum / dayCount);
    }
    return result;
  }

  private getDates() {
    let ret = this.rawData.map(function (item) {
      return item[0];
    });
    return ret;
  }

  private getData() {
    let ret = this.rawData.map(function (item) {
      return [+item[1], +item[2], +item[5], +item[6]];
    });
    return ret;
  }


  ngOnInit() {
    this.options = {
      backgroundColor: '#21202D',
      legend: {
        data: ['日K', 'MA5', 'MA10', 'MA20', 'MA30'],
        inactiveColor: '#777',
        textStyle: {
          color: '#fff'
        }
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          animation: false,
          type: 'cross',
          lineStyle: {
            color: '#376df4',
            width: 2,
            opacity: 1
          }
        }
      },
      xAxis: {
        type: 'category',
        data: this.getDates(),
        axisLine: {lineStyle: {color: '#8392A5'}}
      },
      yAxis: {
        scale: true,
        axisLine: {lineStyle: {color: '#8392A5'}},
        splitLine: {show: false}
      },
      grid: {
        bottom: 80
      },
      dataZoom: [{
        textStyle: {color: '#8392A5'},
        handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.' +
        '4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,' +
        '11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
        handleSize: '80%',
        dataBackground: {
          areaStyle: {
            color: '#8392A5'
          },
          lineStyle: {
            opacity: 0.8,
            color: '#8392A5'
          }
        },
        handleStyle: {
          color: '#fff',
          shadowBlur: 3,
          shadowColor: 'rgba(0, 0, 0, 0.6)',
          shadowOffsetX: 2,
          shadowOffsetY: 2
        }
      }, {
        type: 'inside'
      }],
      animation: false,
      series: [
        {
          type: 'candlestick',
          name: '日K',
          data: this.rawData.map(function (item) {
            return [+item[1], +item[2], +item[5], +item[6]];
          }),
          itemStyle: {
            normal: {
              color: '#FD1050',
              color0: '#0CF49B',
              borderColor: '#FD1050',
              borderColor0: '#0CF49B'
            }
          }
        },
        {
          name: 'MA5',
          type: 'line',
          data: this.calculateMA(5, this.getData()),
          smooth: true,
          showSymbol: false,
          lineStyle: {
            normal: {
              width: 1
            }
          }
        },
        {
          name: 'MA10',
          type: 'line',
          data: this.calculateMA(10, this.getData()),
          smooth: true,
          showSymbol: false,
          lineStyle: {
            normal: {
              width: 1
            }
          }
        },
        {
          name: 'MA20',
          type: 'line',
          data: this.calculateMA(20, this.getData()),
          smooth: true,
          showSymbol: false,
          lineStyle: {
            normal: {
              width: 1
            }
          }
        },
        {
          name: 'MA30',
          type: 'line',
          data: this.calculateMA(30, this.getData()),
          smooth: true,
          showSymbol: false,
          lineStyle: {
            normal: {
              width: 1
            }
          }
        }
      ]
    };
  }
}